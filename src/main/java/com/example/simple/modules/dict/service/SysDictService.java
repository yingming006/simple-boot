package com.example.simple.modules.dict.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.exception.BusinessException;
import com.example.simple.modules.dict.converter.SysDictDataConverter;
import com.example.simple.modules.dict.converter.SysDictTypeConverter;
import com.example.simple.modules.dict.dto.SysDictDataCreateDTO;
import com.example.simple.modules.dict.dto.SysDictDataUpdateDTO;
import com.example.simple.modules.dict.dto.SysDictTypeCreateDTO;
import com.example.simple.modules.dict.dto.SysDictTypeUpdateDTO;
import com.example.simple.modules.dict.entity.SysDictDataEntity;
import com.example.simple.modules.dict.entity.SysDictTypeEntity;
import com.example.simple.modules.dict.mapper.SysDictDataMapper;
import com.example.simple.modules.dict.mapper.SysDictTypeMapper;
import com.example.simple.modules.dict.vo.SysDictDataVO;
import com.example.simple.modules.dict.vo.SysDictTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典服务
 */
@Service
@RequiredArgsConstructor
public class SysDictService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private final SysDictTypeConverter dictTypeConverter = SysDictTypeConverter.INSTANCE;
    private final SysDictDataConverter dictDataConverter = SysDictDataConverter.INSTANCE;

    private static final String DICT_CACHE_KEY_PREFIX = "sys_dict_data:";

    // ==================== 字典类型管理 ====================

    public List<SysDictTypeVO> listDictTypes() {
        return dictTypeMapper.selectList(null).stream()
                .map(dictTypeConverter::toVO).collect(Collectors.toList());
    }

    @Transactional
    public void createDictType(SysDictTypeCreateDTO dto) {
        LambdaQueryWrapper<SysDictTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictTypeEntity::getType, dto.getType());
        if (dictTypeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("字典类型已存在");
        }
        dictTypeMapper.insert(dictTypeConverter.fromCreateDTO(dto));
    }

    @Transactional
    public void updateDictType(Long id, SysDictTypeUpdateDTO dto) {
        SysDictTypeEntity entity = dictTypeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("字典类型不存在");
        }
        dictTypeConverter.updateFromDTO(dto, entity);
        dictTypeMapper.updateById(entity);
    }

    @Transactional
    public void deleteDictType(Long id) {
        // 删除类型前，应检查是否有数据关联
        SysDictTypeEntity entity = dictTypeMapper.selectById(id);
        if (entity == null) return;

        LambdaQueryWrapper<SysDictDataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictDataEntity::getDictType, entity.getType());
        if (dictDataMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该字典类型下存在数据，无法删除");
        }
        dictTypeMapper.deleteById(id);
    }

    // ==================== 字典数据管理 ====================

    /**
     * 根据字典类型获取字典数据列表（带缓存）。
     * @param dictType 字典类型
     * @return 字典数据VO列表
     */
    public List<SysDictDataVO> getDataByType(String dictType) {
        String cacheKey = DICT_CACHE_KEY_PREFIX + dictType;

        // 1. 从缓存获取
        List<SysDictDataEntity> cachedData = (List<SysDictDataEntity>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            return cachedData.stream().map(dictDataConverter::toVO).collect(Collectors.toList());
        }

        // 2. 缓存未命中，查询数据库
        LambdaQueryWrapper<SysDictDataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictDataEntity::getDictType, dictType);
        wrapper.orderByAsc(SysDictDataEntity::getSortOrder);
        List<SysDictDataEntity> dbData = dictDataMapper.selectList(wrapper);

        // 3. 写入缓存（即使为空也写入，防止缓存穿透）
        if (dbData == null) dbData = Collections.emptyList();
        redisTemplate.opsForValue().set(cacheKey, dbData);

        return dbData.stream().map(dictDataConverter::toVO).collect(Collectors.toList());
    }

    @Transactional
    public void createDictData(SysDictDataCreateDTO dto) {
        dictDataMapper.insert(dictDataConverter.fromCreateDTO(dto));
        // 使缓存失效
        clearDictCache(dto.getDictType());
    }

    @Transactional
    public void updateDictData(Long id, SysDictDataUpdateDTO dto) {
        SysDictDataEntity entity = dictDataMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("字典数据不存在");
        }
        dictDataConverter.updateFromDTO(dto, entity);
        dictDataMapper.updateById(entity);
        // 使缓存失效
        clearDictCache(entity.getDictType());
    }

    @Transactional
    public void deleteDictData(Long id) {
        SysDictDataEntity entity = dictDataMapper.selectById(id);
        if (entity == null) return;

        dictDataMapper.deleteById(id);
        // 使缓存失效
        clearDictCache(entity.getDictType());
    }

    /**
     * 清除指定类型的字典缓存。
     * @param dictType 字典类型
     */
    private void clearDictCache(String dictType) {
        redisTemplate.delete(DICT_CACHE_KEY_PREFIX + dictType);
    }
}