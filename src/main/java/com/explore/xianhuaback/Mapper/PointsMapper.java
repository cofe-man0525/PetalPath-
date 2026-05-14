package com.explore.xianhuaback.Mapper;

import com.explore.xianhuaback.DTO.AdminPoint.AdminPointDTO;
import com.explore.xianhuaback.Entity.AdminPoints.AdminPointsVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PointsMapper {

    Boolean insertImageUrl(String newUrl);

    @Select("select  id from adminPoints where image_url=#{url}")
    Long getId(String url);

    List<AdminPointsVO> getList();

    List<AdminPointsVO> searchByKeyword(@Param("keyword") String keyword);

    List<AdminPointsVO> getById(@Param("id") Long id);

    //传递过来的数据进行插入数据库
    Boolean addDataPoints(AdminPointDTO adminPointDTO);

    Boolean editDataPoints(AdminPointDTO adminPointDTO);

    @Delete("DELETE FROM adminPoints WHERE id = #{id}")
    Boolean deleteId( @Param("id") Long id);

    @Update("UPDATE adminPoints SET image_url = #{url} WHERE id = #{id}")
    Boolean updateImageUrl(@Param("url") String url, @Param("id") Long id);


    //这里根据的是扣减库存的情况
    @Update("UPDATE adminPoints SET point_number = point_number - 1 " +
            "WHERE id = #{couponId} AND point_number > 0")
    int decreaseStock(String couponId);


    @Update("UPDATE adminPoints SET point_number = point_number + 1 " +
            "WHERE id = #{couponId} AND point_number > 0")
    void increaseStock(String couponId);
}
