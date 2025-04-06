package com.xht.program.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rabbitmq.client.Channel;
import com.xht.model.entity.comment.CommentLikes;
import com.xht.model.entity.comment.Comments;
import com.xht.model.event.CommentCreateEvent;
import com.xht.model.event.CommentLikeEvent;
import com.xht.program.config.CommentMQConfig;
import com.xht.program.mapper.CommentMapper;
import com.xht.program.service.CommentLikeService;
import com.xht.program.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-05 12:21
 **/

@Component
@Slf4j
public class CommentConsumer {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentLikeService commentLikeService;

    @Autowired
    private CommentMapper commentMapper;



    @RabbitListener(queues = {CommentMQConfig.COMMENT_LIKE_QUEUE})
    @Transactional
    public void commentLikeListener(CommentLikeEvent commentLikeEvent, Message message, Channel channel){
      log.info("点赞：收到消息"+commentLikeEvent.toString());

        try {
            if (commentLikeEvent.isLike()){
                CommentLikes commentLikes = new CommentLikes();
                BeanUtils.copyProperties(commentLikeEvent,commentLikes);
                commentLikeService.save(commentLikes);
                commentService.incLikeCount(commentLikeEvent.getCommentId());
            }else {
                commentLikeService.remove(Wrappers.<CommentLikes>lambdaQuery().eq(CommentLikes::getCommentId,commentLikeEvent.getCommentId())
                        .eq(CommentLikes::getUserId,commentLikeEvent.getUserId()));
                commentService.decLikeCount(commentLikeEvent.getCommentId());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @RabbitListener(queues = {CommentMQConfig.COMMENT_CREATE_QUEUE})
    @Transactional
    public void commentCreateListener(CommentCreateEvent commentCreateEvent, Message message, Channel channel){
        try {
            log.info("创建评论：收到消息");
            Comments comments = new Comments();
            BeanUtils.copyProperties(commentCreateEvent,comments);
            commentService.save(comments);
            commentMapper.updateParent(comments.getParentId());
            if (!Objects.equals(comments.getParentId(), comments.getReplyToId())){
                commentMapper.updateReplyTo(comments.getReplyToId());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
