package com.xht.program.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xht.model.entity.seckill.SeckillActivity;
import com.xht.model.vo.seckill.SKActivityVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xht
 * @since 2025-04-06
 */
public interface SeckillActivityService extends IService<SeckillActivity> {

    List<SKActivityVo> activities(Page<List<SKActivityVo>> listPage);
}
