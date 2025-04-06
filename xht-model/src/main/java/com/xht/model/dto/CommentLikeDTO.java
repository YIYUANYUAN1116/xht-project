package com.xht.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-05 10:49
 **/

@Data
public class CommentLikeDTO {
    @NotNull
    private Long commentId;
    @NotNull
    private Long userId;

}
