package com.xht.program.controller;

import com.xht.model.entity.bigfile.FileInfo;
import com.xht.model.vo.common.Result;
import com.xht.program.service.FileInfoService;
import com.xht.program.service.FileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-07 14:36
 **/
@RestController
@RequestMapping("/file")
@Tag(name = "文件接口")
@Slf4j
public class BigFileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private MinioClient minioClient;

    @Operation(summary = "检查文件是否存在")
    @GetMapping("/checkFile")
    public Result checkFile(@RequestParam("fileMd5") String fileMd5){
        return Result.success(fileService.checkFile(fileMd5));
    }

    @Operation(summary = "分块文件上传前的检测")
    @PostMapping("/checkchunk")
    public Result checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {

        return Result.success(fileService.checkChunk(fileMd5,chunk));
    }

    @Operation(summary = "上传分块文件")
    @PostMapping("/uploadchunk")
    public Result uploadChunk(@RequestParam("file") MultipartFile file,
                                             @RequestParam("fileMd5") String fileMd5,
                                             @RequestParam("chunk") int chunk) throws Exception {

        return Result.success(fileService.uploadChunk(file,fileMd5,chunk));
    }

    @Operation(summary = "合并文件")
    @PostMapping("/mergechunks")
    public Result mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                             @RequestParam("fileName") String fileName,
                                             @RequestParam("chunkTotal") int chunkTotal) throws Exception {

        return Result.success(fileService.mergechunks(fileMd5,fileName,chunkTotal));

    }

    @GetMapping("/download/{fileId}")
    public void downloadFile(@PathVariable String fileId, HttpServletResponse response) {
        FileInfo fileInfo = fileInfoService.getById(fileId);
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("chunks")
                        .object(fileId)
                        .build())) {

            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8));
            response.setContentType("application/octet-stream");
            IOUtils.copy(is, response.getOutputStream());
        }catch (Exception e){
            log.error("下载失败："+e.getMessage());
        }
    }
}
