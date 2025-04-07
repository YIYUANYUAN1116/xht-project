package com.xht.model.entity.bigfile;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xht.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xht
 * @since 2025-04-07
 */
@Getter
@Setter
@ToString
@TableName("file_info")
public class FileInfo  {

    private static final long serialVersionUID = 1L;

    /**
     * 文件md5
     */
    @TableId(value = "id",type = IdType.INPUT)
    private String id;

    /**
     * 文件名
     */
    private String fileName;


    /**
     * 文件bucket
     */
    private String bucket;


    /**
     * 文件路径
     */
    private String filePath;


    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 分片大小
     */
    private Integer chunkSize;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 0-上传中 1-已完成 2-未上传
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT) //创建时自动填充
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE) //创建时自动填充
    private Date updateTime;

    @Schema(description = "是否删除")
    private Integer isDeleted;
}
