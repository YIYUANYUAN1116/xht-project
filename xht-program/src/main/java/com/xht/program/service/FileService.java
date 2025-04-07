package com.xht.program.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-07 14:38
 **/
public interface FileService {
    Boolean checkFile(String fileMd5);

    Boolean checkChunk(String fileMd5, int chunk);

    Boolean uploadChunk(MultipartFile file, String fileMd5, int chunk);

    Boolean mergechunks(String fileMd5, String fileName, int chunkTotal);
}
