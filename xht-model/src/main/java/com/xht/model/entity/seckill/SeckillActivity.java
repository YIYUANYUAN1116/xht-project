package com.xht.model.entity.seckill;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xht.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

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
@TableName("seckill_activity")
public class SeckillActivity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
