package com.example.simple.modules.config.controller;

import com.example.simple.common.GlobalResponse;
import com.example.simple.modules.config.dto.SysConfigUpdateDTO;
import com.example.simple.modules.config.service.SysConfigService;
import com.example.simple.modules.config.vo.SysConfigVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置管理
 * @author yingm
 */
@RestController
@RequestMapping("/configs")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /**
     * 获取所有系统配置列表
     * @return 系统配置列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('configs:list')")
    public GlobalResponse<List<SysConfigVO>> listAll() {
        return GlobalResponse.success(sysConfigService.listAll());
    }

    /**
     * 根据配置键获取配置值
     * @param key 配置键
     * @return 配置值
     */
    @GetMapping("/{key}")
    @PreAuthorize("hasAuthority('configs:list')")
    public GlobalResponse<String> getValueByKey(@PathVariable String key) {
        return GlobalResponse.success(sysConfigService.getValueByKey(key));
    }

    /**
     * 根据配置键更新配置值
     * @param key       配置键
     * @param updateDTO 更新数据
     * @return 操作结果
     */
    @PutMapping("/{key}")
    @PreAuthorize("hasAuthority('configs:update')")
    public GlobalResponse<Void> updateConfigByKey(@PathVariable String key, @Valid @RequestBody SysConfigUpdateDTO updateDTO) {
        sysConfigService.updateConfigByKey(key, updateDTO);
        return GlobalResponse.success();
    }
}