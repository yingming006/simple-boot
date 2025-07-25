package com.example.simple.modules.dict.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SysDictTypeCreateDTO {
    @NotBlank(message = "字典名称不能为空")
    private String name;
    @NotBlank(message = "字典类型不能为空")
    private String type;
    private Integer status;
    private String remark;
}