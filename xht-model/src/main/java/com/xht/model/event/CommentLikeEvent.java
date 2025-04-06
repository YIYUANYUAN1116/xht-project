package com.xht.model.event;

import lombok.Data;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-05 12:25
 **/
@Data
public class CommentLikeEvent {

    private Long commentId;

    private Long userId;

    private boolean isLike;
}
