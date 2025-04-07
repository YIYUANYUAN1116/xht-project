package com.xht.model.vo.bigfile;

import lombok.Data;

import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-07 16:27
 **/
@Data
public class CheckFileVo {
    private String fileMd5;
    private String fileName;
    private Integer status;
    private List<Integer> chunkList;
}
