package com.xht.program.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xht.model.entity.seckill.SeckillActivity;
import com.xht.model.vo.seckill.SKActivityVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xht
 * @since 2025-04-06
 */
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    List<SKActivityVo> activities(Page<List<SKActivityVo>> listPage);
}
