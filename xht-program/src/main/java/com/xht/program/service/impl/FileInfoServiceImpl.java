package com.xht.program.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xht.model.entity.bigfile.FileInfo;
import com.xht.program.mapper.FileInfoMapper;
import com.xht.program.service.FileInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xht
 * @since 2025-04-07
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

}
