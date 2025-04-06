package com.xht.model.dto;

import lombok.Data;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-05 00:21
 **/

@Data
public class CommentCreateDTO {
    private Long articleId;

    private Long userId;

    private Long parentId;

    private Long replyToId;

    private String content;
}
