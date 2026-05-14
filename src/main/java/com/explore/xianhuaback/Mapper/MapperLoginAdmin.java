package com.explore.xianhuaback.Mapper;

import com.explore.xianhuaback.Entity.AdminLogin;
import com.explore.xianhuaback.Entity.CodeAdmin;
import com.explore.xianhuaback.Entity.SubmitAdmin;
import com.explore.xianhuaback.Entity.editPassword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MapperLoginAdmin {

    //从库表中获取到用户id
    @Select("select id from adminlogin where username=#{userName}")
    Long getUserId(@Param("userName") String userName);


    AdminLogin getAdminLogin(AdminLogin adminLogin);

    //查找对应的是否存在
    SubmitAdmin getSubmitAdmin(@Param("userName") String userName);

    //将数据进行插入到注册表中
    void insertSubmitAdmin(SubmitAdmin submitAdmin);

    //将数据进行插入到登陆表中
    void insertLoginAdmin(SubmitAdmin submitAdmin);

    @Select("select * from  adminlogin where  username=#{name}")
    String getAdminLoginUser(String name);

    //修改密码
    void PutAdminPassword(editPassword editPassword);
}
