package com.explore.xianhuaback.Service.admin;

import com.explore.xianhuaback.DTO.AdminActivityDTO.ActivityInputSelectDTO;
import com.explore.xianhuaback.DTO.AdminActivityDTO.AddActivityDTO;
import com.explore.xianhuaback.DTO.AdminActivityDTO.EditActivityImagesDTO;
import com.explore.xianhuaback.Entity.AdminAddActivity.ActivityVO;
import com.explore.xianhuaback.Entity.AdminAddActivity.AddActivity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ActivityService {
    //向数据库中插入图片地址
    Long insertImagesUrl(String imageUrl);

    //根据id进行插入数据
    String IdinsertList(AddActivityDTO addActivityDTO);

    //获取到对应的数据列表
    List<ActivityVO> getByIdList();

    List<ActivityVO> SelectInputList(ActivityInputSelectDTO activityInputSelectDTO);

    //指定的对应的id进行删除
    String deleteID(Integer id);

    //根据id进行返回数据
    ActivityVO getListById(Integer id);

    //根据对应的地址和id进行修改操作
    String editUrlImage(EditActivityImagesDTO editActivityImagesDTO, MultipartFile file);

    //得到新的地址和id进行将原来的地址进行修改
    String updateImageUrl(String url, Long id);

    //编辑数据进行修改的操作
    String upDataListIdActivity(AddActivityDTO addActivityDTO);
}
