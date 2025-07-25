package com.example.simple.modules.log.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.vo.PageVO;
import com.example.simple.modules.log.converter.LogConverter;
import com.example.simple.modules.log.domain.SysOperationLog;
import com.example.simple.modules.log.dto.LogDTO;
import com.example.simple.modules.log.mapper.OperationLogMapper;
import com.example.simple.modules.log.vo.LogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperationLogService extends ServiceImpl<OperationLogMapper, SysOperationLog> {

    private final OperationLogMapper operationLogMapper;
    private final LogConverter logConverter = LogConverter.INSTANCE;

    @Transactional(readOnly = true)
    public PageVO<LogVO> getLogPage(LogDTO queryDTO, PageQueryDTO pageQueryDTO) {
        Page<SysOperationLog> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(queryDTO.getUsername()), SysOperationLog::getUsername, queryDTO.getUsername());
        wrapper.like(StringUtils.isNotBlank(queryDTO.getOperation()), SysOperationLog::getOperation, queryDTO.getOperation());
        wrapper.ge(queryDTO.getStartTime() != null, SysOperationLog::getCreateTime, queryDTO.getStartTime());
        wrapper.le(queryDTO.getEndTime() != null, SysOperationLog::getCreateTime, queryDTO.getEndTime());
        wrapper.orderByDesc(SysOperationLog::getCreateTime);

        Page<SysOperationLog> logPage = operationLogMapper.selectPage(page, wrapper);

        return PageVO.of(logPage.convert(logConverter::toLogVO));
    }
}