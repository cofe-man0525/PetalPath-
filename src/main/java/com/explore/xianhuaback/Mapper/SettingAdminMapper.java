package com.explore.xianhuaback.Mapper;

import com.explore.xianhuaback.Entity.AdminSetting.AdminSetting;
import com.explore.xianhuaback.Entity.AdminSetting.SettingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SettingAdminMapper {

    //修改数据类型
    Boolean editSetting(AdminSetting adminSetting);

    //根据id来进行修改名字
    SettingVO getListById();

}
