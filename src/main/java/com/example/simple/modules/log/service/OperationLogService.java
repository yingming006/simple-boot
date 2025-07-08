package com.example.simple.modules.log.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.simple.modules.log.domain.SysOperationLog;
import com.example.simple.modules.log.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, SysOperationLog> {
}