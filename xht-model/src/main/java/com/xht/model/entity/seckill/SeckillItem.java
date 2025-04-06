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
@TableName("seckill_item")
public class SeckillItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联活动ID
     */
    private Long activityId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 剩余库存
     */
    private Integer stock;

    /**
     * 原始库存
     */
    private Integer originalStock;

    /**
     * 乐观锁版本号
     */
    private Integer version;


    /**
     * 秒杀价格
     */
    private BigDecimal price;

    /**
     * 原始价格
     */
    private BigDecimal originalPrice;
}
