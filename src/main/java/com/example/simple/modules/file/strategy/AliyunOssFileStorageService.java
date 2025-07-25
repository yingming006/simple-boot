package com.example.simple.modules.file.strategy;

import com.example.simple.modules.file.FileStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务的阿里云OSS实现（存根）。
 */
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "aliyun-oss")
public class AliyunOssFileStorageService implements FileStorageService {

    @Override
    public String upload(MultipartFile file) throws IOException {
        throw new UnsupportedOperationException("阿里云OSS存储功能尚未实现");
    }
}