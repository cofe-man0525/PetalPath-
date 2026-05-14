package com.explore.xianhuaback.Mapper;


import com.explore.xianhuaback.Entity.AdminFlowers.EditList;
import com.explore.xianhuaback.Entity.AdminFlowers.FlowersGetData;
import com.explore.xianhuaback.Entity.AdminFlowers.GetComboData;
import com.explore.xianhuaback.Entity.FlowersAdd;
import com.explore.xianhuaback.Entity.FlowersGetVO;
import com.explore.xianhuaback.Entity.PageGet;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface FlowersMapper {



    int insertImageUrl(String imageUrl);

    @Select("select id from adminflowers where image_url=#{imageUrl}")
    Long getId(String imageUrl);


    int insertFlowers(@Param("flowersAdd") FlowersAdd flowersAdd,
                      @Param("id") Integer id);

    //查询总条数
    Long getTotalList(PageGet page);

    //分页查询情况
    List<FlowersGetVO> getSelectList(PageGet pageGet);

    @Select("select * from adminflowers where id=#{id}")
    FlowersGetVO getListId(Long id);

    @Delete("DELETE FROM adminflowers WHERE id = #{id}")
    Boolean deleteId(Long id);

    //根据id来查询数据层
    FlowersGetVO getByListId(Integer id);

    //根据id来进行修改数据
    Boolean editUploadImageUrl(Long id, String url);

    Boolean editList(EditList editList);

    FlowersGetData getByIdFlowersGoods(String comboId);

    GetComboData getByIdCombo(String goodsId);
}
