package com.xht.model.event;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 16:33
 **/
@Data
public class SecKillOrderEven {
    private Long userId;
    private Long activityId;
    private Long itemId;
    private Integer itemNum;
    private BigDecimal price;
}
