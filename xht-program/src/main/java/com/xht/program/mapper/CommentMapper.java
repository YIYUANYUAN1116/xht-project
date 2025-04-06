package com.xht.program.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xht.model.entity.comment.Comments;
import com.xht.model.vo.comment.CommentVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:43
 **/
@Mapper
public interface CommentMapper extends BaseMapper<Comments> {
    List<CommentVo> selectPageCommentVo(IPage<CommentVo> commentsIPage, Long articleId);

    void incLikeCount(Long commentId);

    void decLikeCount(Long commentId);

    List<CommentVo> getPreviewReplies(List<Long> parentIds);

    void updateParent(Long parentId);

    void updateReplyTo(Long replyToId);

    List<CommentVo> selectAllSubPageCommentVo(IPage<CommentVo> commentsIPage, Long commentId);
}
