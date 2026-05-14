package com.explore.xianhuaback.Controller.admin;

import com.explore.xianhuaback.DTO.AdminFlowersDTO.AdminSettingDTO;
import com.explore.xianhuaback.Entity.AdminSetting.SettingVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.admin.SettingAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/setting")
public class SettingAdminController {

    @Autowired
    private SettingAdminService  settingAdminService;

    @PostMapping("/edit")
    public Result<SettingVO> editListGet(@RequestBody AdminSettingDTO  adminSettingDTO) {


        if (adminSettingDTO == null) {
            log.info("传递过来的数据是空的");
            throw new RuntimeException("运行出来的情况时空的");
        }

        SettingVO settingVO=settingAdminService.editSetting(adminSettingDTO);
        if (settingVO == null) {
            throw new RuntimeException("返回来的数据是空");
        }
        return Result.success(settingVO);

    }
    @GetMapping("/getList")
    //数据的回显
    public Result<SettingVO> getList(){
        log.info("开始进行回显的操作");
        SettingVO settingVO= settingAdminService.getList();
        if (settingVO == null) {
            throw new RuntimeException("回显操作有问题");
        }
        return Result.success(settingVO);
    }

}
