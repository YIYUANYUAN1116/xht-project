package com.xht.model.vo.seckill;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 15:23
 **/
@Data
public class SKItemVo {
    private Long id;
    private Long itemId;
    private String name;
    private Integer stock;
    private Integer originalStock;
    private BigDecimal price;
    private BigDecimal originalPrice;
}
