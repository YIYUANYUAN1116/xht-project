package com.xht.model.entity.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:36
 **/

@Data
@Schema(description = "comment_likes")
public class CommentLikes {
    private static final long serialVersionUID = 1L;

    @Schema(description = "评论Id")
    private  Long commentId;

    @Schema(description = "用户Id")
    private Long userId;

}
