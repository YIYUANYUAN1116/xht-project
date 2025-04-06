package com.xht.program.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xht.model.contant.RedisKeyConst;
import com.xht.model.dto.SKCreateActivityDTO;
import com.xht.model.dto.SKCreateItemDTO;
import com.xht.model.entity.seckill.SeckillActivity;
import com.xht.model.entity.seckill.SeckillItem;
import com.xht.model.event.SecKillOrderEven;
import com.xht.model.vo.seckill.SKActivityVo;
import com.xht.program.config.mq.OrderMQConfig;
import com.xht.program.service.SeckillActivityService;
import com.xht.program.service.SeckillItemService;
import com.xht.program.service.SeckillService;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 14:40
 **/

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Autowired
    private SeckillItemService seckillItemService;

    @Override
    @Transactional
    public void createActivity(SKCreateActivityDTO skCreateActivityDTO) {
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtils.copyProperties(skCreateActivityDTO,seckillActivity);
        seckillActivityService.save(seckillActivity);
        List<SKCreateItemDTO> skCreateItemDTOS = skCreateActivityDTO.getSkCreateItemDTOS();
        List<SeckillItem> seckillItems = skCreateItemDTOS.stream().map(item -> {
            SeckillItem seckillItem = new SeckillItem();
            seckillItem.setActivityId(seckillActivity.getId());
            BeanUtils.copyProperties(item, seckillItem);
            return seckillItem;
        }).toList();
        seckillItemService.saveBatch(seckillItems);
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(RedisKeyConst.SECKILL_ACTIVITY_KEY + seckillActivity.getId());
        seckillItems.forEach(item->{
            String itemKey = RedisKeyConst.SECKILL_STOCK_KEY+seckillActivity.getId()+":"+item.getId();
            SKCreateItemDTO skCreateItemDTO = new SKCreateItemDTO();
            BeanUtils.copyProperties(item,skCreateItemDTO);
            skCreateItemDTO.setStartTime(skCreateActivityDTO.getStartTime());
            skCreateItemDTO.setEndTime(skCreateActivityDTO.getEndTime());
            String jsonString = JSON.toJSONString(skCreateItemDTO);

            Boolean aBoolean = boundHashOps.hasKey(jsonString);
            if (!aBoolean){
                boundHashOps.put(String.valueOf(item.getId()),jsonString);
                //使用信号量
                RSemaphore semaphore = redissonClient.getSemaphore(itemKey);
                semaphore.trySetPermits(item.getStock());
            }
        });
    }

    @Override
    public List<SKActivityVo> activities(Integer page,Integer size) {
        Page<List<SKActivityVo>> listPage = new Page<>();
        listPage.setSize(size);
        listPage.setCurrent(page);
        return seckillActivityService.activities(listPage);
    }

    @Override
    public void doSeckill(Long activityId, Long itemId,Long userId) throws InterruptedException {
        //todo 1.校验略(活动时间，用户是否合法等)
        //2.获取库存
        //3.有库存占位,没库存抛异常
        //4.占位成功扣减库存,发送消息异步处理，占位失败抛异常
        BoundHashOperations<String, String, String> boundHashOps =
                redisTemplate.boundHashOps(RedisKeyConst.SECKILL_ACTIVITY_KEY + activityId);
        String item = boundHashOps.get(String.valueOf(itemId));
        SKCreateItemDTO skCreateItemDTO = JSON.parseObject(item, SKCreateItemDTO.class);

        String itemKey = RedisKeyConst.SECKILL_STOCK_KEY+activityId+":"+itemId;
        Integer seckillCount = (Integer) redisTemplate.opsForValue().get(itemKey);
        if (seckillCount>0){
            String userKey = "user:"+":"+userId+":"+itemKey;
            Boolean absent = redisTemplate.opsForValue().setIfAbsent(userKey, "1",
                    skCreateItemDTO.getEndTime().toInstant(ZoneOffset.ofTotalSeconds(8 * 60 * 60)).toEpochMilli() - skCreateItemDTO.getStartTime().toInstant(ZoneOffset.ofTotalSeconds(8 * 60 * 60)).toEpochMilli(), TimeUnit.MILLISECONDS);
            if (absent){

                RSemaphore semaphore = redissonClient.getSemaphore(itemKey);
                boolean semaphoreCount = semaphore.tryAcquire(1, Duration.ofMillis(100));
                if (semaphoreCount){
                    //占用库存成功，发送消息
                    SecKillOrderEven secKillOrderEven = new SecKillOrderEven();
                    secKillOrderEven.setActivityId(activityId);
                    secKillOrderEven.setItemId(itemId);
                    secKillOrderEven.setItemNum(1);
                    secKillOrderEven.setPrice(skCreateItemDTO.getPrice());
                    secKillOrderEven.setUserId(userId);
                    rabbitTemplate.convertAndSend(OrderMQConfig.ORDER_EVENT_EXCHANGE,OrderMQConfig.SEC_KILL_ROUTE_KEY,secKillOrderEven);
                }
            }else {
                throw new RuntimeException("已经参与过活动");
            }
        }else {
            throw new RuntimeException("库存不足");
        }
    }
}
