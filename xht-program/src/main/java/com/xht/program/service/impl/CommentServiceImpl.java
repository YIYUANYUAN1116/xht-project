package com.xht.program.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xht.model.contant.RedisConst;
import com.xht.model.dto.CommentCreateDTO;
import com.xht.model.dto.CommentLikeDTO;
import com.xht.model.entity.comment.Comments;
import com.xht.model.event.CommentCreateEvent;
import com.xht.model.event.CommentLikeEvent;
import com.xht.model.vo.comment.CommentVo;
import com.xht.program.config.CommentMQConfig;
import com.xht.program.mapper.CommentMapper;
import com.xht.program.service.CommentService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:41
 **/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comments>  implements CommentService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public List<CommentVo>  getComments(Long articleId, Integer page, Integer size) {
        IPage<CommentVo> commentsIPage = new Page<>();
        commentsIPage.setCurrent(page);
        commentsIPage.setSize(size);
        List<CommentVo> commentVos = baseMapper.selectPageCommentVo(commentsIPage, articleId);
        List<Long> idList = commentVos.stream().map(CommentVo::getId).toList();
        List<CommentVo> previewReplies = baseMapper.getPreviewReplies(idList);
        Map<Long, List<CommentVo>> listMap =
                previewReplies.stream().collect(Collectors.groupingBy(CommentVo::getParentId));
        for (CommentVo commentVo : commentVos) {
            commentVo.setChildCommentVoList(listMap.get(commentVo.getId()));
        }
        return commentVos;
    }

    @Override
    public void create(CommentCreateDTO commentCreateDTO) {
        CommentCreateEvent comments = new CommentCreateEvent();
        BeanUtils.copyProperties(commentCreateDTO,comments);
        Message message = new Message(JSON.toJSONBytes(comments));
        rabbitTemplate.convertAndSend(CommentMQConfig.COMMENT_CREATE_EXCHANGE,CommentMQConfig.COMMENT_CREATE_ROUTE_KEY,message);}

    @Override
    public void like(CommentLikeDTO commentLikeDTO) {
        String relateKey = RedisConst.COMMENT_LIKES_KEY+commentLikeDTO.getCommentId();
        String countKey = RedisConst.COMMENT_LIKES_COUNT_KEY+commentLikeDTO.getCommentId();

        // 使用 Lua 脚本保证原子性（检查用户是否点赞并更新计数器）
        String luaScript = """
            local isLiked = redis.call('HGET', KEYS[1], ARGV[1])
            if (isLiked == '1') then
                redis.call('HDEL', KEYS[1], ARGV[1])
                redis.call('DECR', KEYS[2])
                return 0
            else
                redis.call('HSET', KEYS[1], ARGV[1], '1')
                redis.call('INCR', KEYS[2])
                return 1
            end
            """;

        RedisScript<Long> redisScript = RedisScript.of(luaScript, Long.class);
        Long execute = redisTemplate.execute(redisScript, Arrays.asList(relateKey, countKey), String.valueOf(commentLikeDTO.getUserId()));
        CommentLikeEvent commentLikeEvent = new CommentLikeEvent();
        BeanUtils.copyProperties(commentLikeDTO,commentLikeEvent);
        commentLikeEvent.setLike(execute == 1);
        rabbitTemplate.convertAndSend(CommentMQConfig.COMMENT_LIKE_EXCHANGE, CommentMQConfig.COMMENT_LIKE_ROUTE_KEY,commentLikeEvent);
    }

    @Override
    public void incLikeCount(Long commentId) {
        baseMapper.incLikeCount(commentId);
    }

    @Override
    public void decLikeCount(Long commentId) {
        baseMapper.decLikeCount(commentId);
    }

    @Override
    public List<CommentVo> getAllSubComments(Long commentId, Integer page, Integer size) {
        IPage<CommentVo> commentsIPage = new Page<>();
        commentsIPage.setCurrent(page);
        commentsIPage.setSize(size);
        return baseMapper.selectAllSubPageCommentVo(commentsIPage, commentId);
    }

}
