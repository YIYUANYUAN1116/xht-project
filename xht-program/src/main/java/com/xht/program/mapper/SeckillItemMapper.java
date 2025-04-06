package com.xht.program.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xht.model.entity.seckill.SeckillItem;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xht
 * @since 2025-04-06
 */
public interface SeckillItemMapper extends BaseMapper<SeckillItem> {

    Integer deductStock(Long itemId, Integer version,Integer  itemNum);
}
