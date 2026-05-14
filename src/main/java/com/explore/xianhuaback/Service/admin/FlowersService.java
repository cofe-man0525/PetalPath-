package com.explore.xianhuaback.Service.admin;

import com.explore.xianhuaback.DTO.AdminFlowersDTO.DeleteFlowersDTO;
import com.explore.xianhuaback.DTO.AdminFlowersDTO.EditFlowersUrlDTO;
import com.explore.xianhuaback.DTO.AdminFlowersDTO.EditListDTO;
import com.explore.xianhuaback.DTO.FlowersAddDTO;
import com.explore.xianhuaback.DTO.PageGetDTO;
import com.explore.xianhuaback.Entity.FlowersGetVO;
import com.explore.xianhuaback.Entity.PageResult;
import com.qiniu.common.QiniuException;
import org.springframework.web.multipart.MultipartFile;

public interface FlowersService {
    //将地址存到数据库中
    Long insertImages(String imageUrl);

    //再插入的情况下进行插入数据
    String insertFlowersData(FlowersAddDTO flowersAddDTO);

    //分页查询
    PageResult<FlowersGetVO> getList(PageGetDTO pageGetDTO);

    //删除对应的数据
    String deleteId(DeleteFlowersDTO deleteFlowersDTO) throws QiniuException;

    //根据id来进行数据的查询
    FlowersGetVO getByIdList(Integer id);

    //直接针对于图片进行编辑
    String editUploadUrl(EditFlowersUrlDTO editFlowersUrlDTO, MultipartFile file);

    //编辑操作
    String editList(EditListDTO editLIstDTO);
}
