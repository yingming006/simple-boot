package com.example.simple.modules.log.controller;

import com.example.simple.common.GlobalResponse;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.vo.PageVO;
import com.example.simple.modules.log.dto.LogDTO;
import com.example.simple.modules.log.service.OperationLogService;
import com.example.simple.modules.log.vo.LogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志管理
 * @author yingm
 */
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final OperationLogService logService;

    /**
     * 分页查询操作日志
     * @param queryDTO 查询条件
     * @param pageQueryDTO 分页参数
     * @return 日志分页数据
     */
    @GetMapping
    @PreAuthorize("hasAuthority('logs:list')")
    public GlobalResponse<PageVO<LogVO>> getLogPage(LogDTO queryDTO, PageQueryDTO pageQueryDTO) {
        return GlobalResponse.success(logService.getLogPage(queryDTO, pageQueryDTO));
    }
}