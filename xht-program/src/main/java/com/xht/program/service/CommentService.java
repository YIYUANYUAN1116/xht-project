package com.xht.program.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xht.model.dto.CommentCreateDTO;
import com.xht.model.dto.CommentLikeDTO;
import com.xht.model.entity.comment.Comments;
import com.xht.model.vo.comment.CommentVo;

import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:39
 **/
public interface CommentService extends IService<Comments> {
    List<CommentVo> getComments(Long articleId, Integer page, Integer size);

    void create(CommentCreateDTO commentCreateDTO);

    void like(CommentLikeDTO commentLikeDTO) ;

    void incLikeCount(Long commentId);

    void decLikeCount(Long commentId);

    List<CommentVo> getAllSubComments(Long commentId, Integer page, Integer size);
}
