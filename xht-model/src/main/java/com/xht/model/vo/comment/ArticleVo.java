package com.xht.model.vo.comment;

import lombok.Data;

import java.util.List;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:55
 **/

@Data
public class ArticleVo {
    private Long articleId;
    private List<CommentVo> commentVoList;
}
