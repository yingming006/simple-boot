package com.example.simple.common.dto;

import lombok.Data;

@Data
public class PageQueryDTO {

    /**
     * 页码，默认为1
     */
    private long pageNum = 1;

    /**
     * 每页数量，默认为10
     */
    private long pageSize = 10;
}