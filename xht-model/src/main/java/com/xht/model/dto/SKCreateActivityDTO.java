package com.xht.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 14:41
 **/
@Data
public class SKCreateActivityDTO {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    List<SKCreateItemDTO> skCreateItemDTOS;
}
