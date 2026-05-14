package com.explore.xianhuaback.Service.admin;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.explore.xianhuaback.DTO.AdminLoginDTO;
import com.explore.xianhuaback.DTO.CodeAdminDTO;
import com.explore.xianhuaback.DTO.SubmitAdminDTO;
import com.explore.xianhuaback.DTO.EditPasswordDTO;

public interface ServiceAdminLogin {
    SaTokenInfo LoginBackAdmin(AdminLoginDTO adminLoginDTO);


    //创建注册功能
    String insertSubmit(SubmitAdminDTO submitAdminDTO);

    String getAdminCode(CodeAdminDTO codeAdminDTO);

    //修改密码
    String editAdmin(EditPasswordDTO editPasswordDTO);
}
