package com.explore.xianhuaback.Service.user;

import com.explore.xianhuaback.DTO.UserLoginDTO.UserAddressEditDTO;
import com.explore.xianhuaback.DTO.UserLoginDTO.UserGetPointsDTO;
import com.explore.xianhuaback.Entity.user.UserGoodsVO;
import com.explore.xianhuaback.Entity.user.UserMessageVO;
import com.explore.xianhuaback.Entity.user.banner;
import com.explore.xianhuaback.Entity.user.getUserAddressVO;
import com.explore.xianhuaback.Entity.userIndex.UserIndexSignVO;

import java.util.List;

public interface IndexUserService {
    List<banner> getFiveImages();

    List<UserGoodsVO> getGoodsList();

    List<getUserAddressVO> getUserAddress(String id);

    getUserAddressVO getByIdAddress(String id);

    void deleteById(String id);

    //进行指令的控制类型
    Boolean editSaveAddress(UserAddressEditDTO userAddressEditDTO);

    //进行数据的传输的情况
    Boolean addAddress(UserAddressEditDTO userAddressEditDTO);

    Boolean getCheckPoints(UserGetPointsDTO userGetPointsDTO);

    //进行逻辑层的数据的调用情况
    List<UserIndexSignVO> getHistoryPoints(String userId);

    //根据签到的时候插入积分
    UserMessageVO insertPoints(String userId);

    void insertPointsWeek(String userId);

    void insertPointsMonth(String userId);
}
