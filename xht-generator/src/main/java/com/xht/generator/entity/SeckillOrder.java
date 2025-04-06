package com.xht.generator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xht.model.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
     * 订单号
     */
    private String orderNo;

    /**
     * 状态（0-未支付，1-已支付，2-已取消，3-已超时）
     */
    private Byte status;
}
