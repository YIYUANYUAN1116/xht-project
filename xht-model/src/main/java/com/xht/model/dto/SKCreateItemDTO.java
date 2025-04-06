package com.xht.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 14:50
 **/
@Data
public class SKCreateItemDTO {
    private Long itemId;
    private String name;
    private Integer stock;
    private Integer originalStock;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
