package com.explore.xianhuaback.Mapper;

import com.explore.xianhuaback.Entity.AdminAddActivity.ActivityVO;
import com.explore.xianhuaback.Entity.AdminAddActivity.AddActivity;
import com.explore.xianhuaback.Entity.AdminAddActivity.SelectInput;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ActivityMapper {

    //根据id来进行插入数据情况
    Boolean insertUrlImages(String imageUrl);

    @Select("select id from activity where image_url=#{imageUrl}")
    Long getById(String imageUrl);

    //根据id进行插入数据
    Boolean IdInsertList(AddActivity addActivity);

    @Select("select * from activity")
    List<ActivityVO> getByIdList();

    //根据查询来返回数据
    List<ActivityVO> SelectInput(SelectInput selectInput);

    //根据id进行删除数据
    @Delete("DELETE FROM activity WHERE id = #{id}")
    Boolean deleteId(Integer id);

    @Select("select * from activity where id=#{id}")
    ActivityVO getIdListBy(Integer id);

    //根据传递传递过来的id和新的地址进行修改操作
    @Update("UPDATE activity SET image_url = #{url} WHERE id = #{id}")
    Boolean updateImagesUrl(String url, Long id);

    Boolean upDataDataList(AddActivity addActivity);
}
