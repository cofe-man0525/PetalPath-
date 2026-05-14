package com.explore.xianhuaback.Mapper.user;

import com.explore.xianhuaback.Entity.user.LoginUserVO;
import com.explore.xianhuaback.Entity.user.UserMessageVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginUserMapper {
    @Select("select * from user where open_id=#{openId}")
    LoginUserVO getByOpenId(String openId);

    //进行判断的情况
    Boolean insertLoginUser(LoginUserVO loginUser);

    @Select("select id from user where open_id=#{openId}")
    Long getUserId(String openId);

    @Delete( "DELETE FROM user WHERE id = #{id}")
    Boolean deleteData(Long id);

    @Select("select id from user where open_id=#{id}")
    Long getUserMessage(String id);

    @Select("select id from userTotalNumber where id=#{ids}")
    Long getUserTotalNumberId(Long ids);

    @Select("select * from userTotalNumber where user_id=#{ids}")
    UserMessageVO  getByTotalNumberUser(Long ids);

    //查询的情况进行检查对应的情况
    Boolean insertUserMessage(Long userID);
}
