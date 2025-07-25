package com.example.simple.modules.dict.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SysDictTypeUpdateDTO {
    @NotBlank(message = "字典名称不能为空")
    private String name;
    private Integer status;
    private String remark;
}