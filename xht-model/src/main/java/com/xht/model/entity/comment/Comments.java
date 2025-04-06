package com.xht.model.entity.comment;

import com.xht.model.entity.base.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:32
 **/

@Data
@Schema(description = "comments")
public class Comments extends BaseEntity{

    private static final long serialVersionUID = 1L;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "回复Id")
    private Long replyToId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "回复数")
    private Long replyCount;

    @Schema(description = "状态（1-正常 0-删除）")
    private Boolean status;

}
