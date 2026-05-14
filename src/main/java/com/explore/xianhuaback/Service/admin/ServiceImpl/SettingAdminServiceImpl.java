package com.explore.xianhuaback.Service.admin.ServiceImpl;

import com.explore.xianhuaback.DTO.AdminFlowersDTO.AdminSettingDTO;
import com.explore.xianhuaback.Entity.AdminSetting.AdminSetting;
import com.explore.xianhuaback.Entity.AdminSetting.SettingVO;
import com.explore.xianhuaback.Mapper.SettingAdminMapper;
import com.explore.xianhuaback.Service.admin.SettingAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class SettingAdminServiceImpl  implements SettingAdminService {

    @Autowired
    private SettingAdminMapper settingAdminMapper;

    //实现对应的数据管理
    @Override
    public SettingVO editSetting(AdminSettingDTO adminSettingDTO) {

        if (adminSettingDTO == null) {
            throw new RuntimeException("传递过来的数据是空的");
        }
        //进行数据的传递
        AdminSetting adminSetting = new AdminSetting();
        //进行数据的转换
        BeanUtils.copyProperties(adminSettingDTO,adminSetting);
        adminSetting.setCreatedAt(LocalDateTime.now()); //注册时间
        adminSetting.setUpdatedAt(LocalDateTime.now()); //更新时间

        //判断对应的情况是否存在
        Boolean flag=settingAdminMapper.editSetting(adminSetting);

        if(flag==true){
           SettingVO settingVO=settingAdminMapper.getListById();
           if(settingVO==null){
               log.info("返回来的数据是空的");
               throw new RuntimeException("查询到的数据为空");
           }else{
               return settingVO;
           }
        }else{
           throw new RuntimeException("运行失败，更新失败");
        }


    }

    //数据的回显
    @Override
    public SettingVO getList() {
        return settingAdminMapper.getListById();
    }


}
