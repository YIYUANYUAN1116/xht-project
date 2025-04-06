package com.xht.program.service;

import com.xht.model.dto.SKCreateActivityDTO;
import com.xht.model.vo.seckill.SKActivityVo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 14:40
 **/
public interface SeckillService {
    void createActivity(SKCreateActivityDTO skCreateActivityDTO);

    List<SKActivityVo> activities(Integer page,Integer size);

    void doSeckill(Long activityId, Long itemI,Long userId) throws InterruptedException;
}
