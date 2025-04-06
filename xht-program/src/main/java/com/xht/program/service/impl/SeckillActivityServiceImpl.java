package com.xht.program.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xht.model.entity.seckill.SeckillActivity;
import com.xht.model.vo.seckill.SKActivityVo;
import com.xht.program.mapper.SeckillActivityMapper;
import com.xht.program.service.SeckillActivityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xht
 * @since 2025-04-06
 */
@Service
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityMapper, SeckillActivity> implements SeckillActivityService {

    @Override
    public List<SKActivityVo> activities(Page<List<SKActivityVo>> listPage) {
        return baseMapper.activities(listPage);
    }
}
