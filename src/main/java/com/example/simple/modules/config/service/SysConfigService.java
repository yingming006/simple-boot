package com.example.simple.modules.config.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.config.converter.SysConfigConverter;
import com.example.simple.modules.config.dto.SysConfigUpdateDTO;
import com.example.simple.modules.config.entity.SysConfigEntity;
import com.example.simple.modules.config.mapper.SysConfigMapper;
import com.example.simple.modules.config.vo.SysConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务
 */
@Service
@RequiredArgsConstructor
public class SysConfigService {

    private final SysConfigMapper sysConfigMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SysConfigConverter sysConfigConverter = SysConfigConverter.INSTANCE;

    private static final String CONFIG_CACHE_KEY_PREFIX = "sys_config:";

    /**
     * 根据配置键获取配置值。
     * 优先从缓存读取，缓存未命中则查询数据库并写入缓存。
     *
     * @param key 配置键
     * @return 配置值
     */
    public String getValueByKey(String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }

        String cacheKey = CONFIG_CACHE_KEY_PREFIX + key;

        // 1. 从缓存获取
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            return (String) cachedValue;
        }

        // 2. 缓存未命中，查询数据库
        LambdaQueryWrapper<SysConfigEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfigEntity::getConfigKey, key);
        SysConfigEntity configEntity = sysConfigMapper.selectOne(wrapper);

        if (configEntity == null) {
            // 如果数据库中也不存在，可以根据业务决定是返回 null 还是抛出异常
            return null;
        }

        String value = configEntity.getConfigValue();

        // 3. 将结果写入缓存
        redisTemplate.opsForValue().set(cacheKey, value);

        return value;
    }

    /**
     * 更新一个系统配置。
     *
     * @param key       待更新的配置键
     * @param updateDTO 更新数据
     */
    @Transactional
    public void updateConfigByKey(String key, SysConfigUpdateDTO updateDTO) {
        LambdaQueryWrapper<SysConfigEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfigEntity::getConfigKey, key);
        SysConfigEntity configEntity = sysConfigMapper.selectOne(wrapper);

        if (configEntity == null) {
            throw new BusinessException("配置项不存在: " + key);
        }

        configEntity.setConfigValue(updateDTO.getConfigValue());
        configEntity.setRemark(updateDTO.getRemark());
        sysConfigMapper.updateById(configEntity);

        // 更新数据库后，使缓存失效
        String cacheKey = CONFIG_CACHE_KEY_PREFIX + key;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 获取所有系统配置的列表。
     * @return 配置列表
     */
    public List<SysConfigVO> listAll() {
        List<SysConfigEntity> configList = sysConfigMapper.selectList(null);
        return configList.stream()
                .map(sysConfigConverter::toVO)
                .collect(Collectors.toList());
    }
}