package com.xht.program.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xht.model.entity.comment.CommentLikes;
import com.xht.program.mapper.CommentLikesMapper;
import com.xht.program.service.CommentLikeService;
import org.springframework.stereotype.Service;


/**
 * @Program: xht-project
 * @Description:
 * @Author: YIYUANYUAN
 * @Create: 2025-04-04 23:46
 **/
@Service
public class CommentLikeServiceImpl extends ServiceImpl<CommentLikesMapper, CommentLikes> implements CommentLikeService {
}
