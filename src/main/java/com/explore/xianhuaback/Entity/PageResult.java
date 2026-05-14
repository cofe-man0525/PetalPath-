package com.explore.xianhuaback.Entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private List<T> records;     // 数据列表
    private Long total;           // 总记录数
    private Integer page;         // 当前页码
    private Integer pageSize;     // 每页条数
    private Integer pages;        // 总页数

    // 正确的构造函数
    public PageResult(List<T> records, Long total, Integer page, Integer pageSize) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.pages = total != null ? (int) Math.ceil((double) total / pageSize) : 0;
    }
}
