package com.xht.model.entity.bigfile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xht.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@TableName("file_chunk")
public class FileChunk extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联文件ID
     */
    private String fileId;

    /**
     * 分片序号
     */
    private Integer chunkNumber;

    /**
     * 当前分片大小
     */
    private Long chunkSize;

    /**
     * MinIO返回的ETag
     */
    private String etag;
}
