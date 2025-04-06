package com.xht.model.entity.seckill;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xht.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author xht
 * @since 2025-04-06
 */
@Getter
@Setter
@ToString
@TableName("seckill_order")
public class SeckillOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 活动ID
     */
    private Long activityId;


    /**
     * 商品数量
     */
    private Integer itemNum;


    /**
     * 单价
     */
    private BigDecimal price;


    /**
     * 总价
     */
    private BigDecimal sumPrice;
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 状态（0-未支付，1-已支付，2-已取消，3-已超时）
     */
    private Integer status;
}
