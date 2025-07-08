package com.example.simple.common.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页视图对象，用于向前端返回标准的分页数据结构。
 * @param <T> 数据列表的类型
 */
@Data
public class PageVO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页的数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 每页记录数
     */
    private long pageSize;

    /**
     * 私有构造函数，防止直接实例化
     */
    private PageVO() {}

    /**
     * 从MyBatis-Plus的IPage对象创建PageVO
     * @param page IPage对象
     * @return PageVO实例
     */
    public static <T> PageVO<T> of(IPage<T> page) {
        PageVO<T> vo = new PageVO<>();
        vo.setList(page.getRecords());
        vo.setTotal(page.getTotal());
        vo.setPageNum(page.getCurrent());
        vo.setPageSize(page.getSize());
        return vo;
    }
}