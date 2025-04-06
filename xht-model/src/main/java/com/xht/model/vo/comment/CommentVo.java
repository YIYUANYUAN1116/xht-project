package com.xht.model.vo.comment;

import com.xht.model.entity.comment.Comments;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:53
 **/
@Data
public class CommentVo {
    private Long id;
    private Long articleId;
    private Long userId;
    private Long parentId;
    private Integer replyToId;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private List<CommentVo> childCommentVoList;
}
