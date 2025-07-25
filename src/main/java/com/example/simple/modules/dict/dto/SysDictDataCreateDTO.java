package com.example.simple.modules.dict.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SysDictDataCreateDTO {
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
    @NotBlank(message = "字典标签不能为空")
    private String label;
    @NotBlank(message = "字典键值不能为空")
    private String value;
    private Integer sortOrder;
    private Integer status;
}