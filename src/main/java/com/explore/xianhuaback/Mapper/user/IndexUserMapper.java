package com.explore.xianhuaback.Mapper.user;

import com.explore.xianhuaback.DTO.UserLoginDTO.UserAddressEditDTO;
import com.explore.xianhuaback.Entity.user.UserGoodsVO;
import com.explore.xianhuaback.Entity.user.UserMessageVO;
import com.explore.xianhuaback.Entity.user.banner;
import com.explore.xianhuaback.Entity.user.getUserAddressVO;
import com.explore.xianhuaback.Entity.userIndex.UserIndexSignVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IndexUserMapper {

    @Select("select * from userbanner")
    List<banner> getFiveImages();

    @Select("select * from adminFlowers")
    List<UserGoodsVO> getGoodsList();

    //根据用户id来查询地址情况
    @Select("select * from user_address where user_id=#{id}")
    List<getUserAddressVO> getUserAddress(String id);

    @Select("select * from user_address where id=#{id}")
    getUserAddressVO  getByIdAddress(String id);

    @Delete("DELETE FROM user_address WHERE id = #{id} ")
    int deleteById(String id);

    //进行编辑地址的操作
    Boolean editSaveAddress(UserAddressEditDTO object);

    //进行保存地址的操作
    Boolean addAddress(UserAddressEditDTO userAddressEditDTO);

    //根据好对应的默认的去情况进行将针对的用户id进行修改的操作
    Boolean editAddressIsDefault(UserAddressEditDTO userAddressEditDTO);

    //根据用户id来进行插入数据的情况、
    Boolean insertPointsSign(String userId);

    //根据的是用户的id来进行查询数据的情况
    @Select("SELECT * FROM user_sign_record WHERE user_id = #{userId} AND sign_date = CURDATE()")
    UserIndexSignVO getSignPoints(String userId);

    //返回历史积分的情况
    List<UserIndexSignVO> getHistoryPoints(String userId);
    //插入总积分的数据进行添加积分
    @Update("UPDATE userTotalNumber set total_points=total_points+5 where user_id=#{userId} ")
    Boolean insertPoints(String userId);

    //再次进行查询数据的情况返回总数量的情况
    @Select("select * from userTotalNumber where user_id=#{userId}")
    UserMessageVO getByTotalNumberUser(String userId);

    @Update("UPDATE userTotalNumber set total_points=total_points+20 where user_id=#{userId} ")
    Boolean insertPointsWeek(String userId);

    @Update("UPDATE userTotalNumber set total_points=total_points+100where user_id=#{userId} ")
    Boolean insertPointsMonth(String userId);
}
