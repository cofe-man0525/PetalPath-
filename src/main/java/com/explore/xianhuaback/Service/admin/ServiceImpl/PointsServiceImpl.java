package com.explore.xianhuaback.Service.admin.ServiceImpl;

import com.explore.xianhuaback.DTO.AdminPoint.AdminPointDTO;
import com.explore.xianhuaback.DTO.AdminPoint.EditPointsImagesDTO;
import com.explore.xianhuaback.Entity.AdminPoints.AdminPoints;
import com.explore.xianhuaback.Entity.AdminPoints.AdminPointsVO;
import com.explore.xianhuaback.Mapper.PointsMapper;
import com.explore.xianhuaback.Service.admin.PointsService;
import com.explore.xianhuaback.Utils.QiNiuUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private PointsMapper pointsMapper;

    @Autowired
    private QiNiuUploader  qiNiuUploader;

    @Override
    public String uploadImages(MultipartFile file, String folderName) {
        if(file==null || folderName==null){
            log.info("传递过来的数据是空的");
            throw new RuntimeException("传递过来的参数是空的");
        }
        String NewUrl=qiNiuUploader.uploadActivityImages(file,folderName);
        if(NewUrl==null){
            log.info("传递过来的数据地址是空的查看数据控制台");
            throw new RuntimeException("运行失败");

        }else{
           //同时向数据库进行插入数据
            Boolean flag=pointsMapper.insertImageUrl(NewUrl);
            if(flag==true){
                log.info("传递过来的是真的");
                return NewUrl;
            }else{
                throw new RuntimeException("返回的布尔为错误的");
            }

        }
    }

    //返回来对应的id情况
    @Override
    public Long getByIdImages(String url) {
        if(url==null){
            log.info("传递过来的地址没有正确的放入其中");
            throw new RuntimeException("没有传递过来正确的地址");
        }
        Long id=pointsMapper.getId(url);
        if(id==null){
            log.info("传递过来的id是空的");
            throw new RuntimeException("传递过来的数据是空的");
        }else{
            return id;
        }


    }

    //积分的兑换方式
    @Override
    public List<AdminPointsVO> getList() {
        log.info("进行全部的数据的返回");
        List<AdminPointsVO> adminPointsList=pointsMapper.getList();
        if(adminPointsList==null){
            throw new RuntimeException("查看数据库情况");
        }else{
            return adminPointsList;
        }
    }

    @Override
    public List<AdminPointsVO> searchByKeyword(String keyword) {
        log.info("按关键词查询积分数据");
        List<AdminPointsVO> list = pointsMapper.searchByKeyword(keyword);
        if (list == null) {
            throw new RuntimeException("查看数据库情况");
        }
        return list;
    }

    @Override
    public List<AdminPointsVO> getById(Long id) {
        if (id == null) {
            log.info("传递过来的id是空的");
            throw new RuntimeException("传递过来的id是空的");
        }
        List<AdminPointsVO> list = pointsMapper.getById(id);
        if (list == null) {
            throw new RuntimeException("查看数据库情况");
        }
        return list;
    }

    //根据id进行传递
    @Override
    public String addDataPoints(AdminPointDTO adminPointDTO){
        if(adminPointDTO==null){
            log.info("验证的情况");
            throw new RuntimeException("验证的情况");
        }

        Boolean flag=pointsMapper.addDataPoints(adminPointDTO);
        if(flag==true){
            return "success";
        }else{
            return "error";
        }

    }

    @Override
    public String editDataPoints(AdminPointDTO adminPointDTO) {
        if (adminPointDTO == null || adminPointDTO.getId() == null) {
            log.info("编辑积分：id 不能为空");
            throw new RuntimeException("传递过来的数据是空的或缺少 id");
        }
        Boolean flag = pointsMapper.editDataPoints(adminPointDTO);
        if (Boolean.TRUE.equals(flag)) {
            return "success";
        }
        return "error";
    }

    //根据id继续删除数据
    @Override
    public String deleteId(Long id) {
        if(id==null){
            log.info("传递过来的id是空的");
            throw new RuntimeException("传递过来的id是空的");
        }

        Boolean flag=pointsMapper.deleteId(id);
        if(flag==true){
            return "success";
        }else{
            return "error";
        }
    }

    @Override
    public String editUrlImage(EditPointsImagesDTO dto, MultipartFile file) {
        if (dto == null) {
            log.info("传递过来的数据是空的");
            throw new RuntimeException("传递过来的数据是空的");
        }
        String url = qiNiuUploader.editNewUploadUrl(dto.getUrl(), file, dto.getFolder());
        if (url == null) {
            log.info("传递");
            throw new RuntimeException("传递过来的地址是不存在的");
        }
        return url;
    }

    @Override
    public String updateImageUrl(String url, Long id) {
        if (id == null || url == null) {
            log.info("传递过啊里的数据为空");
            throw new RuntimeException("传递过来的数据参数为空");
        }
        Boolean flag = pointsMapper.updateImageUrl(url, id);
        if (Boolean.TRUE.equals(flag)) {
            return "success";
        }
        return "error";
    }
}
