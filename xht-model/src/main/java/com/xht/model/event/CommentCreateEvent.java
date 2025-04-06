package com.xht.model.event;

import lombok.Data;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-05 16:00
 **/

@Data
public class CommentCreateEvent {
    private Long articleId;

    private Long userId;

    private Long parentId;

    private Long replyToId;

    private String content;
}
