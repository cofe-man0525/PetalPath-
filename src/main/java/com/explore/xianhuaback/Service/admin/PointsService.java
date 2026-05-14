package com.explore.xianhuaback.Service.admin;

import com.explore.xianhuaback.DTO.AdminPoint.AdminPointDTO;
import com.explore.xianhuaback.DTO.AdminPoint.EditPointsImagesDTO;
import com.explore.xianhuaback.Entity.AdminPoints.AdminPoints;
import com.explore.xianhuaback.Entity.AdminPoints.AdminPointsVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PointsService {
    //根据地址和id进行插入数据
    String uploadImages(MultipartFile file, String folderName);

    //再根据返回来的图片地址给它id
    Long getByIdImages(String url);

    List<AdminPointsVO> getList();

    List<AdminPointsVO> searchByKeyword(String keyword);

    List<AdminPointsVO> getById(Long id);

    //传递过来的数据进行添加
    String addDataPoints(AdminPointDTO adminPointDTO);

    /** 编辑：根据 id 更新名称、积分规则等（JSON 与 AdminPointDTO 一致） */
    String editDataPoints(AdminPointDTO adminPointDTO);

    //根据id进行删除
    String deleteId(Long id);

    String editUrlImage(EditPointsImagesDTO dto, MultipartFile file);

    String updateImageUrl(String url, Long id);
}
