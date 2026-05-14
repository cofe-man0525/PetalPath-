package com.explore.xianhuaback.Service.admin;

import com.explore.xianhuaback.DTO.AdminFlowersDTO.AdminSettingDTO;
import com.explore.xianhuaback.Entity.AdminSetting.SettingVO;

public interface SettingAdminService {
    //进行能够实时修改数据
    SettingVO editSetting(AdminSettingDTO adminSettingDTO);

    SettingVO getList();

}
