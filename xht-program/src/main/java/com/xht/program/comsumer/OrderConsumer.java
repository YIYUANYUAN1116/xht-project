package com.xht.program.comsumer;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rabbitmq.client.Channel;
import com.xht.model.entity.seckill.SeckillOrder;
import com.xht.model.enume.StatusEnum;
import com.xht.program.config.mq.OrderMQConfig;
import com.xht.program.mapper.SeckillOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 17:48
 **/
@Component
@Slf4j
public class OrderConsumer {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;


    @RabbitListener(queues = {OrderMQConfig.ORDER_DEAD_QUEUE})
    @Transactional
    public void commentLikeListener(SeckillOrder seckillOrder, Message message, Channel channel){
        log.info("订单死信队列：收到消息"+seckillOrder.toString());
        try {
            SeckillOrder order = seckillOrderMapper.selectById(seckillOrder.getId());
            if (!order.getStatus().equals(StatusEnum.ORDER_PAID.getCode())){
                log.info("订单超时未支付，orderId:{},status:{}",order.getId(),order.getStatus());
                seckillOrderMapper.update(Wrappers.<SeckillOrder>lambdaUpdate()
                        .eq(SeckillOrder::getId,order.getId())
                        .set(SeckillOrder::getStatus,StatusEnum.ORDER_OVERTIME.getCode()));
                //todo 恢复库存等
            }else {
                log.info("订单已支付，orderId:{},status:{}",order.getId(),order.getStatus());
                //todo 后续业务流程
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
