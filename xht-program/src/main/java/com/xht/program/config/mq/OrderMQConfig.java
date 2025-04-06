package com.xht.program.config.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-06 17:39
 **/

@Configuration
public class OrderMQConfig {

    public static final String ORDER_DELAY_QUEUE = "orderDelayQueue";

    public static final String ORDER_EVENT_EXCHANGE = "orderEventExchange";

    public static final String ORDER_DELAY_ROUTE_KEY = "orderDelayRouting";

    public static final String ORDER_DEAD_EXCHANGE= "orderDeadExchange";

    public static final String ORDER_DEAD_QUEUE = "orderDeadQueue";


    private static final String ORDER_DEAD_ROUTE_KEY = "oderDead.key";

    public static final String SEC_KILL_QUEUE = "secKillQueue";

    public static final String SEC_KILL_ROUTE_KEY = "secKillRouting";

    @Bean(ORDER_DELAY_QUEUE)
    public Queue orderDelayQueue(){
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .withArgument("x-message-ttl",30000) //30分钟：30*60*60*1000
                .deadLetterExchange(ORDER_DEAD_EXCHANGE)
                .deadLetterRoutingKey(ORDER_DEAD_ROUTE_KEY).build();
    }


    @Bean(ORDER_EVENT_EXCHANGE)
    public Exchange orderEventExchange(){
        return ExchangeBuilder.topicExchange(ORDER_EVENT_EXCHANGE).durable(true).build();
    }

    @Bean(ORDER_DELAY_ROUTE_KEY)
    public Binding orderDelayRouting(){
        return BindingBuilder.bind(orderDelayQueue()).to(orderEventExchange()).with(ORDER_DELAY_ROUTE_KEY).noargs();
    }


    @Bean(ORDER_DEAD_QUEUE)
    public Queue orderDeadQueue(){
        return QueueBuilder.durable(ORDER_DEAD_QUEUE).build();
    }


    @Bean(ORDER_DEAD_EXCHANGE)
    public Exchange orderDeadExchange(){
        return ExchangeBuilder.topicExchange(ORDER_DEAD_EXCHANGE).durable(true).build();
    }

    @Bean(ORDER_DEAD_ROUTE_KEY)
    public Binding orderDeadRouting(){
        return BindingBuilder.bind(orderDeadQueue()).to(orderDeadExchange()).with(ORDER_DEAD_ROUTE_KEY).noargs();
    }




    @Bean(SEC_KILL_QUEUE)
    public Queue secKillQueue(){
        return QueueBuilder.durable(SEC_KILL_QUEUE).build();
    }


    @Bean(SEC_KILL_ROUTE_KEY)
    public Binding secKillRouting(){
        return BindingBuilder.bind(secKillQueue()).to(orderEventExchange()).with(SEC_KILL_ROUTE_KEY).noargs();
    }
}
