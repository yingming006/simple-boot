package com.example.simple.modules.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务统一接口
 * 定义了所有文件存储策略（如本地存储、云存储）必须遵循的行为。
 */
public interface FileStorageService {

    /**
     * 上传文件
     *
     * @param file 待上传的文件
     * @return 文件的可访问URL
     * @throws IOException 文件处理异常
     */
    String upload(MultipartFile file) throws IOException;
}