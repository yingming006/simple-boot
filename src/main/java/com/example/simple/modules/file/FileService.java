package com.example.simple.modules.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-url-prefix}")
    private String accessUrlPrefix;

    /**
     * 保存上传的文件到本地，并返回其可访问的URL。
     *
     * @param file 上传的文件
     * @return 文件的可访问URL
     * @throws IOException 文件读写异常
     */
    public String saveUploadedFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM"));

        File uploadDir = new File(uploadPath, datePath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new IOException("创建目录失败");
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

        return accessUrlPrefix + datePath + "/" + newFileName;
    }
}