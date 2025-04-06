package com.xht.generator.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xht.model.entity.base.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author xht
 * @since 2025-04-05
 */
@Getter
@Setter
@ToString
@TableName("comment_closure")
public class CommentClosure extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId("ancestor")
    private Long ancestor;

    @TableId("descendant")
    private Long descendant;

    private Integer depth;
}
