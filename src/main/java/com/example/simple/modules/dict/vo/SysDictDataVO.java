package com.example.simple.modules.dict.vo;
import lombok.Data;
@Data
public class SysDictDataVO {
    private Long id;
    private String dictType;
    private String label;
    private String value;
    private Integer sortOrder;
    private Integer status;
}