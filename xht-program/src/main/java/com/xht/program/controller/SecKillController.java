package com.xht.program.controller;

import com.xht.model.dto.SKCreateActivityDTO;
import com.xht.model.vo.common.Result;
import com.xht.model.vo.seckill.SKActivityVo;
import com.xht.program.service.SeckillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 14:37
 **/

@RestController
@RequestMapping("/secKill")
@Tag(name = "秒杀接口")
public class SecKillController {

    @Autowired
    private SeckillService seckillService;

    @PostMapping("/createActivity")
    @Operation(summary = "创建秒杀活动")
    public Result createActivity(@RequestBody SKCreateActivityDTO skCreateActivityDTO){
        seckillService.createActivity(skCreateActivityDTO);
        return Result.success();
    }


    @GetMapping("/activities")
    @Operation(summary = "获取所有秒杀活动")
    public Result<List<SKActivityVo>> getAllActivity(@RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer size){
        return Result.success(seckillService.activities(page,size));
    }


    @GetMapping("/doSeckill")
    @Operation(summary = "秒杀")
    public Result doSeckill(@RequestParam Long activityId,@RequestParam Long itemId,@RequestParam Long userId) throws InterruptedException {
        seckillService.doSeckill(activityId,itemId,userId);
        return Result.success();
    }
}
