package com.explore.xianhuaback.Service.user;

import com.explore.xianhuaback.DTO.UserLoginDTO.UserLoginDTO;
import com.explore.xianhuaback.Entity.user.TokenMessagesVO;
import com.explore.xianhuaback.Entity.user.UserMessageVO;
import com.explore.xianhuaback.Result.Result;

public interface UserLoginService {

    //获取登录权限
    Result<TokenMessagesVO> getLoginMessage(UserLoginDTO userLoginDTO);

    //根据用户id来查询过来的数据情况
    UserMessageVO getUserMessage(String id);
}
