package com.example.simple.modules.file;

import com.example.simple.modules.user.UserService;
import com.example.simple.interceptor.UserContext;
import com.example.simple.common.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;
    private final UserService userService;

    /**
     * @param file 需要上传的任意文件
     * @return 文件的可访问URL
     * @throws IOException 文件保存异常
     */
    @PostMapping("/file")
    public GlobalResponse<String> uploadGenericFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileService.saveUploadedFile(file);

        return GlobalResponse.success(fileUrl);
    }

    /**
     * 上传当前登录用户的头像。
     * 此接口需要登录认证。
     * @param file 上传的图片文件
     * @return 新头像的可访问URL
     * @throws IOException 文件保存异常
     */
    @PostMapping("/avatar")
    public GlobalResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        Long userId = UserContext.getUserId();

        String avatarUrl = fileService.saveUploadedFile(file);

        userService.updateAvatar(userId, avatarUrl);

        return GlobalResponse.success(avatarUrl);
    }
}