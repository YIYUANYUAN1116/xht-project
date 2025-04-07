package com.xht.program.service.impl;



import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xht.model.entity.bigfile.FileChunk;
import com.xht.model.entity.bigfile.FileInfo;
import com.xht.program.service.FileChunkService;
import com.xht.program.service.FileInfoService;
import com.xht.program.service.FileService;
import io.minio.*;

import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-07 14:38
 **/
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private FileChunkService fileChunkService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bigBucket;

    @Override
    public Boolean checkFile(String fileMd5) {
        log.info("fileMd5:"+fileMd5);
        //查数据库
        FileInfo fileInfo = fileInfoService.getById(fileMd5);
        if (fileInfo != null){
            //查minio
            String bucket = fileInfo.getBucket();
            String filePath = fileInfo.getFilePath();
            GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucket).object(filePath).build();
            try (InputStream inputStream = minioClient.getObject(objectArgs)) {
                if (inputStream != null) {
                    return true;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public Boolean checkChunk(String fileMd5, int chunkIndex) {
        //获取文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        //查minio
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bigBucket).object(chunkFilePath).build()
        )) {
            if (inputStream != null) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public Boolean uploadChunk(MultipartFile file, String fileMd5, int chunk) {
        try {
            //创建临时文件
            File tempFile = File.createTempFile("minio", "temp");
            //上传的文件拷贝到临时文件
            file.transferTo(tempFile);
            //文件路径
            String absolutePath = tempFile.getAbsolutePath();
            //分片路径
            String chunkFilePath = getChunkFilePath(fileMd5, chunk);
            String mimeType = getMimeType(null);
            //将文件上传至minio
            String etag = addFilesToMinIO(absolutePath, mimeType, bigBucket, chunkFilePath);
            if (!StringUtils.hasLength(etag)) {
                log.debug("上传分块文件失败:{}", chunkFilePath);
                return false;
            }

            FileChunk fileChunk = new FileChunk();
            fileChunk.setFileId(fileMd5);
            fileChunk.setChunkNumber(chunk);
            fileChunk.setChunkSize(file.getSize());
            fileChunk.setEtag(etag);
            fileChunkService.save(fileChunk);
            log.debug("上传分块文件成功:{}",chunkFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public Boolean mergechunks(String fileMd5, String fileName, int chunkTotal) {
        String fileFolderPath = getChunkFileFolderPath(fileMd5);
        List<ComposeSource> composeSourceList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder().bucket(bigBucket)
                        .object(fileFolderPath.concat(String.valueOf(i))).build()).toList();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        ComposeObjectArgs build = ComposeObjectArgs.builder().bucket(bigBucket).object(mergeFilePath).sources(composeSourceList).build();
        try {
            minioClient.composeObject(build);

        } catch (Exception e) {
           log.error("合并文件失败："+e.getMessage());
            return false;
        }

        // ====验证md5====
        FileInfo fileInfo = new FileInfo();
        File minioFile = downloadFileFromMinIO(bigBucket,mergeFilePath);
        if (minioFile == null){
            log.error("下载合并文件失败：mergeFilePath:{}",mergeFilePath);
            return false;
        }
        try (FileInputStream fileInputStream = new FileInputStream(minioFile);){
            String md5DigestAsHex = DigestUtils.md5DigestAsHex(fileInputStream);
            if (!md5DigestAsHex.equals(fileMd5)){
                log.error("文件md5验证失败：mergeFilePath:{},fileMd5:{},md5DigestAsHex:{}",mergeFilePath,fileMd5,md5DigestAsHex);
            }
            fileInfo.setFileSize(minioFile.length());
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            minioFile.delete();
        }
        //文件入库
        fileInfo.setId(fileMd5);
        fileInfo.setBucket(bigBucket);
        fileInfo.setFilePath(mergeFilePath);
        fileInfo.setStatus(1);
        //=====清除分块文件=====
        clearChunkFiles(fileFolderPath,chunkTotal);
        return true;
    }

    /**
     * 从minio下载文件
     * @param bucket
     * @param objectName
     * @return
     */
    private File downloadFileFromMinIO(String bucket, String objectName) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucket).object(objectName).build();
        File file = null;
        FileOutputStream fileOutputStream = null;
        try(InputStream inputStream = minioClient.getObject(objectArgs);) {
            file = File.createTempFile("minio", ".merge");
            fileOutputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream,fileOutputStream);
            return file;
        }catch (Exception e){
            log.error("下载minio文件失败：bucket{},objectName{},error:{}",bucket,objectName,e.getMessage());
        }finally {
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    log.error("关闭流失败："+e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 得到合并后的文件的地址
     * @param fileMd5 文件id即md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    private String addFilesToMinIO(String localFileName, String mimeType, String bucketName, String minioObjectName) {
        try {
            UploadObjectArgs.Builder builder = UploadObjectArgs.builder();
            builder.bucket(bucketName)
                    .object(minioObjectName).filename(localFileName).build();
            if (StringUtils.hasLength(mimeType)){
                builder.contentType(mimeType);
            }
            ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(builder.build());
            String etag = objectWriteResponse.etag();
            log.info("上传文件到minio成功,bucket:{},objectName:{},etag:{}", bucketName, minioObjectName,etag);
            return etag;
        } catch (Exception e) {
            log.error("文件上传失败："+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }


    private String getChunkFilePath(String fileMd5,int chunkNum) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        return chunkFileFolderPath+chunkNum;
    }

    //根据扩展名取出mimeType
    private String getMimeType(String extension) {
        if (!StringUtils.hasLength(extension)) return "";
        //根据扩展名取出mimeType
        ContentInfo mimeTypeMatch = ContentInfoUtil.findMimeTypeMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (mimeTypeMatch != null) {
            mimeType = mimeTypeMatch.getMimeType();
        }
        return mimeType;
    }

    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        try {
            List<DeleteObject> deleteObject = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> {
                        return new DeleteObject(chunkFileFolderPath.concat(String.valueOf(i)));
                    }).collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket(bigBucket)
                    .objects(deleteObject).build();

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);

            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }
}
