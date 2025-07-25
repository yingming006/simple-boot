package com.example.simple.modules.file;

import com.example.simple.exception.BusinessException;
import com.example.simple.common.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;

    /**
     * @param file 需要上传的任意文件
     * @return 文件的可访问URL
     * @throws IOException 文件保存异常
     */
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponse<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileService.saveUploadedFile(file);

        return GlobalResponse.success(fileUrl);
    }

    /**
     * 多文件上传接口
     *
     * @param files 需要上传的多个文件
     * @return 包含所有上传成功文件的可访问URL列表
     * @throws IOException 文件保存异常
     */
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponse<List<String>> uploadMultipleFiles(@RequestPart("files") MultipartFile[] files) throws IOException {

        if (files == null || files.length == 0) {
            throw new BusinessException("上传失败，请至少选择一个文件");
        }

        List<MultipartFile> validFiles = Arrays.stream(files)
                .filter(file -> !file.isEmpty())
                .toList();

        if (validFiles.isEmpty()) {
            throw new BusinessException("上传失败，所有选中的文件均为空");
        }

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : validFiles) {
            String fileUrl = fileService.saveUploadedFile(file);
            fileUrls.add(fileUrl);
        }

        return GlobalResponse.success(fileUrls);
    }
}