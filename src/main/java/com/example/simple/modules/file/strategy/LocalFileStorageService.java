package com.example.simple.modules.file.strategy;

import com.example.simple.exception.BusinessException;
import com.example.simple.modules.file.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件存储服务的本地实现。
 * 将文件保存到服务器的本地磁盘。
 */
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local")
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.root-path}")
    private String rootPath;

    @Value("${file.access-url-prefix}")
    private String accessUrlPrefix;

    @Override
    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传的文件不能为空");
        }

        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM"));

        File uploadDir = new File(rootPath, datePath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new IOException("创建目录失败: " + uploadDir.getAbsolutePath());
            }
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

        File destFile = new File(uploadDir, newFileName);
        file.transferTo(destFile);

        // 拼接可访问的URL并返回
        String urlPath = datePath + "/" + newFileName;
        return accessUrlPrefix.endsWith("/") ? accessUrlPrefix + urlPath
                : accessUrlPrefix + "/" + urlPath;
    }
}