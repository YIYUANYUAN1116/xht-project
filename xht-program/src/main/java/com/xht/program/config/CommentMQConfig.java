package com.xht.program.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-05 12:09
 **/

@Configuration
public class CommentMQConfig {

    public static final String COMMENT_LIKE_QUEUE = "commentLikeQueue";

    public static final String COMMENT_LIKE_EXCHANGE = "commentLikeExchange";

    public static final String COMMENT_LIKE_ROUTE_KEY = "commentLikeRouting";


    public static final String COMMENT_CREATE_QUEUE = "commentCreateQueue";

    public static final String COMMENT_CREATE_EXCHANGE = "commentCreateExchange";

    public static final String COMMENT_CREATE_ROUTE_KEY = "commentCreateRouting";


    @Bean(COMMENT_LIKE_QUEUE)
    public Queue commentLikeQueue(){
        return QueueBuilder.durable(COMMENT_LIKE_QUEUE).build();
    }

    @Bean(COMMENT_LIKE_EXCHANGE)
    public Exchange commentLikeExchange(){
        return ExchangeBuilder.directExchange(COMMENT_LIKE_EXCHANGE).durable(true).build();
    }


    @Bean
    public Binding commentLikeBinding(){
        return BindingBuilder.bind(commentLikeQueue()).to(commentLikeExchange()).with(COMMENT_LIKE_ROUTE_KEY).and(null);
    }


    @Bean(COMMENT_CREATE_QUEUE)
    public Queue commentCreateQueue(){
        return new Queue(COMMENT_CREATE_QUEUE);
    }

    @Bean(COMMENT_CREATE_EXCHANGE)
    public Exchange commentCreateExchange(){
        return new DirectExchange(COMMENT_CREATE_EXCHANGE,true,false);
    }


    @Bean
    public Binding commentCreateBinding(){

        return BindingBuilder.bind(commentCreateQueue()).to(commentCreateExchange()).with(COMMENT_CREATE_ROUTE_KEY).noargs();
    }

}
