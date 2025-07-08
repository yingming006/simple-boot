package com.example.simple.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageQueryDTO {

    /**
     * 页码，默认为1
     */
    @Schema(description = "页码，从1开始", example = "1")
    private long pageNum = 1;

    /**
     * 每页数量，默认为10
     */
    @Schema(description = "每页显示条数", example = "10")
    private long pageSize = 10;
}