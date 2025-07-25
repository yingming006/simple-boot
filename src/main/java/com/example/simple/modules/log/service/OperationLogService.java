package com.example.simple.modules.log.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.simple.common.dto.PageQueryDTO;
import com.example.simple.common.vo.PageVO;
import com.example.simple.modules.log.converter.LogConverter;
import com.example.simple.modules.log.dto.LogDTO;
import com.example.simple.modules.log.entity.SysOperationLog;
import com.example.simple.modules.log.mapper.OperationLogMapper;
import com.example.simple.modules.log.vo.LogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 操作日志服务
 */
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final LogConverter logConverter = LogConverter.INSTANCE;

    /**
     * 以异步方式保存操作日志。
     * @param entity 日志实体
     */
    @Async("asyncTaskExecutor")
    @Transactional
    public void saveLog(SysOperationLog entity) {
        operationLogMapper.insert(entity);
    }

    /**
     * 分页查询操作日志。
     * @param queryDTO 查询条件
     * @param pageQueryDTO 分页参数
     * @return 日志分页数据
     */
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