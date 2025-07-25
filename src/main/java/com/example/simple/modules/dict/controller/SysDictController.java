package com.example.simple.modules.dict.controller;

import com.example.simple.common.GlobalResponse;
import com.example.simple.modules.dict.dto.SysDictDataCreateDTO;
import com.example.simple.modules.dict.dto.SysDictDataUpdateDTO;
import com.example.simple.modules.dict.dto.SysDictTypeCreateDTO;
import com.example.simple.modules.dict.dto.SysDictTypeUpdateDTO;
import com.example.simple.modules.dict.service.SysDictService;
import com.example.simple.modules.dict.vo.SysDictDataVO;
import com.example.simple.modules.dict.vo.SysDictTypeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理
 */
@RestController
@RequestMapping("/dicts")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService sysDictService;

    // ==================== 字典类型接口 ====================

    /**
     * 获取所有字典类型列表
     * @return 字典类型列表
     */
    @GetMapping("/types")
    @PreAuthorize("hasAuthority('dict_types:list')")
    public GlobalResponse<List<SysDictTypeVO>> listDictTypes() {
        return GlobalResponse.success(sysDictService.listDictTypes());
    }

    /**
     * 新增字典类型
     * @param createDTO 字典类型数据
     * @return 操作结果
     */
    @PostMapping("/types")
    @PreAuthorize("hasAuthority('dict_types:create')")
    public GlobalResponse<Void> createDictType(@Valid @RequestBody SysDictTypeCreateDTO createDTO) {
        sysDictService.createDictType(createDTO);
        return GlobalResponse.success();
    }

    /**
     * 修改字典类型
     * @param id        字典类型ID
     * @param updateDTO 字典类型数据
     * @return 操作结果
     */
    @PutMapping("/types/{id}")
    @PreAuthorize("hasAuthority('dict_types:update')")
    public GlobalResponse<Void> updateDictType(@PathVariable Long id, @Valid @RequestBody SysDictTypeUpdateDTO updateDTO) {
        sysDictService.updateDictType(id, updateDTO);
        return GlobalResponse.success();
    }

    /**
     * 删除字典类型
     * @param id 字典类型ID
     * @return 操作结果
     */
    @DeleteMapping("/types/{id}")
    @PreAuthorize("hasAuthority('dict_types:delete')")
    public GlobalResponse<Void> deleteDictType(@PathVariable Long id) {
        sysDictService.deleteDictType(id);
        return GlobalResponse.success();
    }


    // ==================== 字典数据接口 ====================

    /**
     * 根据字典类型获取字典数据列表
     * 这是一个常用接口，前端通常用它来渲染下拉框等
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @GetMapping("/data/{dictType}")
    @PreAuthorize("hasAuthority('dict_data:list')")
    public GlobalResponse<List<SysDictDataVO>> getDataByType(@PathVariable String dictType) {
        return GlobalResponse.success(sysDictService.getDataByType(dictType));
    }

    /**
     * 新增字典数据
     * @param createDTO 字典数据
     * @return 操作结果
     */
    @PostMapping("/data")
    @PreAuthorize("hasAuthority('dict_data:create')")
    public GlobalResponse<Void> createDictData(@Valid @RequestBody SysDictDataCreateDTO createDTO) {
        sysDictService.createDictData(createDTO);
        return GlobalResponse.success();
    }

    /**
     * 修改字典数据
     * @param id        字典数据ID
     * @param updateDTO 字典数据
     * @return 操作结果
     */
    @PutMapping("/data/{id}")
    @PreAuthorize("hasAuthority('dict_data:update')")
    public GlobalResponse<Void> updateDictData(@PathVariable Long id, @Valid @RequestBody SysDictDataUpdateDTO updateDTO) {
        sysDictService.updateDictData(id, updateDTO);
        return GlobalResponse.success();
    }

    /**
     * 删除字典数据
     * @param id 字典数据ID
     * @return 操作结果
     */
    @DeleteMapping("/data/{id}")
    @PreAuthorize("hasAuthority('dict_data:delete')")
    public GlobalResponse<Void> deleteDictData(@PathVariable Long id) {
        sysDictService.deleteDictData(id);
        return GlobalResponse.success();
    }
}