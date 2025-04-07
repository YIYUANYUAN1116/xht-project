package com.xht.program.comsumer;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rabbitmq.client.Channel;
import com.xht.model.entity.seckill.SeckillItem;
import com.xht.model.entity.seckill.SeckillOrder;
import com.xht.model.enume.StatusEnum;
import com.xht.model.event.SecKillOrderEven;
import com.xht.program.config.mq.OrderMQConfig;
import com.xht.program.mapper.SeckillItemMapper;
import com.xht.program.mapper.SeckillOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 16:45
 **/
@Component
@Slf4j
public class SecKillConsumer {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillItemMapper seckillItemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {OrderMQConfig.SEC_KILL_QUEUE})
    @Transactional
    public void commentLikeListener(SecKillOrderEven secKillOrderEven, Message message, Channel channel) throws IOException {
        log.info("秒杀创建订单：收到消息"+secKillOrderEven.toString());
        try {
            Long activityId = secKillOrderEven.getActivityId();
            Long itemId = secKillOrderEven.getItemId();
            Long userId = secKillOrderEven.getUserId();
            if (seckillOrderMapper.exists(Wrappers.<SeckillOrder>lambdaQuery()
                    .eq(SeckillOrder::getItemId,itemId).eq(SeckillOrder::getActivityId,activityId).eq(SeckillOrder::getUserId,userId))){
                log.info("用户已参加秒杀：activityId:{},itemId:{},userId:{}",activityId,itemId,userId);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }

            //扣库存
            SeckillItem item = seckillItemMapper.selectById(itemId);
            Integer integer =  seckillItemMapper.deductStock(itemId,item.getVersion(),secKillOrderEven.getItemNum());
            if (integer == 0) {
                log.info("扣减库存失败：activityId:{},itemId:{},userId:{},version:{}",activityId,itemId,userId,item.getVersion());
                return;
            }
            //创建订单
            SeckillOrder seckillOrder = new SeckillOrder();
            BeanUtils.copyProperties(secKillOrderEven,seckillOrder);
            seckillOrder.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
            seckillOrder.setSumPrice(seckillOrder.getPrice().multiply(new BigDecimal(seckillOrder.getItemNum())));
            seckillOrder.setStatus(StatusEnum.ORDER_UNPAID.getCode());
            seckillOrderMapper.insert(seckillOrder);

            //发送订单定时队列
            rabbitTemplate.convertAndSend(OrderMQConfig.ORDER_EVENT_EXCHANGE,OrderMQConfig.ORDER_DELAY_ROUTE_KEY,seckillOrder);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            throw new RuntimeException(e);
        }

    }
}
