package com.xht.model.enume;

import lombok.Getter;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 17:33
 **/
@Getter
public enum StatusEnum {

    ORDER_UNPAID( 0, "未支付"),
    ORDER_PAID( 1, "已支付"),
    ORDER_OVERTIME( 2, "超时已取消")
    ;

    private Integer code ;      // 业务状态码
    private String message ;    // 响应消息

    private StatusEnum(Integer code , String message) {
        this.code = code ;
        this.message = message ;
    }

}
