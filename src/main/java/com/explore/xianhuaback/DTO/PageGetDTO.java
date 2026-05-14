package com.explore.xianhuaback.DTO;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

//接收前端的传递过来的参数
@Data
public class PageGetDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //当前页
    private Integer page=1;

    //每页的数据多少
    private Integer pageSize=6;

    //搜索的关键词
    private String keyword;
    //分类情况
    private String category;
    //分页状态
    private Integer status;

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

}
