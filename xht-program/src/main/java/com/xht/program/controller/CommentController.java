package com.xht.program.controller;

import com.xht.model.dto.CommentCreateDTO;
import com.xht.model.dto.CommentLikeDTO;
import com.xht.model.vo.common.Result;
import com.xht.program.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:38
 **/

@RestController
@RequestMapping("/comment")
@Tag(name = "评论接口")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "根据文章ID获取评论")
    @GetMapping("/list/{articleId}")
    public Result list(@PathVariable Long articleId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size){
        return Result.success(commentService.getComments(articleId, page, size));
    }

    @Operation(summary = "获取所有子评论")
    @GetMapping("/list/all/{commentId}")
    public Result listAll(@PathVariable Long commentId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size){
        return Result.success(commentService.getAllSubComments(commentId, page, size));
    }

    @Operation(summary = "根据文章ID创建评论")
    @PostMapping("/create")
    public Result create(@RequestBody CommentCreateDTO commentCreateDTO){
        commentService.create(commentCreateDTO);
        return Result.success();
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/like")
    public Result like(@RequestBody CommentLikeDTO commentLikeDTO){
        commentService.like(commentLikeDTO);
        return Result.success();
    }
}
