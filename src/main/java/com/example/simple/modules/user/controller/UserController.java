package com.example.simple.modules.user.controller;

import com.example.simple.annotation.RequiresRole;
import com.example.simple.common.GlobalResponse;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.vo.PageVO;
import com.example.simple.interceptor.UserContext;
import com.example.simple.modules.user.dto.UserCreateDTO;
import com.example.simple.modules.user.dto.UserDTO;
import com.example.simple.modules.user.dto.UserPasswordUpdateDTO;
import com.example.simple.modules.user.dto.UserUpdateDTO;
import com.example.simple.modules.user.service.UserService;
import com.example.simple.modules.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户的信息
     */
    @GetMapping("/me")
    public GlobalResponse<UserVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        UserVO userVO = userService.getUserInfo(userId);
        return GlobalResponse.success(userVO);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @RequiresRole("ADMIN")
    public GlobalResponse<PageVO<UserVO>> getUserPage(UserDTO queryDTO, PageQueryDTO pageQueryDTO) {
        return GlobalResponse.success(userService.getUserPage(queryDTO, pageQueryDTO));
    }

    /**
     * 新增用户
     */
    @RequiresRole("ADMIN")
    @PostMapping
    public GlobalResponse<Void> create(@Valid @RequestBody UserCreateDTO createDTO) {
        userService.createUser(createDTO);
        return GlobalResponse.success();
    }

    /**
     * 更新指定用户信息
     * @param updateDTO 包含更新信息的数据
     */
    @RequiresRole("ADMIN")
    @PutMapping("/{id}")
    public GlobalResponse<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(id, updateDTO);
        return GlobalResponse.success();
    }

    /**
     * 当前登录用户更新自己的个人资料
     */
    @PutMapping("/me")
    public GlobalResponse<Void> updateProfile(@RequestBody UserUpdateDTO updateDTO) {
        Long currentUserId = UserContext.getUserId();
        userService.updateUser(currentUserId, updateDTO);
        return GlobalResponse.success();
    }

    /**
     * 当前登录用户修改自己的密码
     */
    @PutMapping("/me/password")
    public GlobalResponse<Void> updatePassword(@RequestBody UserPasswordUpdateDTO passwordUpdateDTO) {
        Long currentUserId = UserContext.getUserId();
        userService.updatePassword(currentUserId, passwordUpdateDTO);
        return GlobalResponse.success();
    }

    /**
     * 批量删除用户
     *
     * @param ids ID列表，例如: [1, 2, 3]
     */
    @RequiresRole("ADMIN")
    @DeleteMapping
    public GlobalResponse<Void> delete(@RequestBody List<Long> ids) {
        userService.deleteUsers(ids);
        return GlobalResponse.success();
    }

    /**
     * 根据ID获取单个用户信息
     */
    @GetMapping("/{id}")
    @RequiresRole("ADMIN")
    public GlobalResponse<UserVO> getById(@PathVariable Long id) {
        UserVO userVO = userService.getUserInfo(id);
        return GlobalResponse.success(userVO);
    }
}