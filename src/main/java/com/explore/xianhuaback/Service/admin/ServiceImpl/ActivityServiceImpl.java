package com.explore.xianhuaback.Service.admin.ServiceImpl;

import com.explore.xianhuaback.DTO.AdminActivityDTO.ActivityInputSelectDTO;
import com.explore.xianhuaback.DTO.AdminActivityDTO.AddActivityDTO;
import com.explore.xianhuaback.DTO.AdminActivityDTO.EditActivityImagesDTO;
import com.explore.xianhuaback.Entity.AdminAddActivity.ActivityVO;
import com.explore.xianhuaback.Entity.AdminAddActivity.AddActivity;
import com.explore.xianhuaback.Entity.AdminAddActivity.SelectInput;
import com.explore.xianhuaback.Mapper.ActivityMapper;
import com.explore.xianhuaback.Service.admin.ActivityService;
import com.explore.xianhuaback.Utils.QiNiuUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper  activityMapper;

    @Autowired
    private QiNiuUploader  qiNiuUploader;


    @Override
    public Long insertImagesUrl(String imageUrl){

        if(imageUrl==null){
            log.info("开始进行逻辑层的数据的传递");
            throw new RuntimeException("传递过来的地址是空的");
        }

        Boolean result=activityMapper.insertUrlImages(imageUrl);
        if(result==true){
            //开始进行传递象数层进行传递数据
            Long id=activityMapper.getById(imageUrl);
            if(id==null){
                log.info("返回来的id不存在");
                throw new RuntimeException("返回来的id不存在，请查看对应的数据库");
            }else{
                log.info("传递过来的id展示对应的数据返回给前端");
                return  id;
            }
        }else{
            throw new RuntimeException("插入失败");
        }


    }

    //根据id进行插入数据
    @Override
    public String IdinsertList(AddActivityDTO addActivityDTO) {
        if(addActivityDTO==null){
            log.info("传递开始");
            throw new RuntimeException("传递过来的数据是空的");
        }
        AddActivity addActivity=new AddActivity();
        BeanUtils.copyProperties(addActivityDTO,addActivity); //进行属性的赋值
        Boolean flag=activityMapper.IdInsertList(addActivity);
        if(flag==true){
            return "success";
        }else{
            return  "error";
        }

    }

    //获取到对应的列表
    @Override
    public  List<ActivityVO> getByIdList() {
        log.info("传递开始存在");
        List<ActivityVO> listActivity= activityMapper.getByIdList();
        return listActivity;

    }

    //返回来的数据是空的
    @Override
    public List<ActivityVO> SelectInputList(ActivityInputSelectDTO activityInputSelectDTO) {
                if (activityInputSelectDTO==null){
                    throw new RuntimeException("传递过来的数据是空的");
                }

                try{
                    SelectInput selectInput=new SelectInput();
                    BeanUtils.copyProperties(activityInputSelectDTO,selectInput); //传递过来的情况
                    List<ActivityVO> list=activityMapper.SelectInput(selectInput);
                    if(list==null){
                        log.info("传递过来的数据是空的");
                        throw new RuntimeException("返回来的数据是空的");
                    }else{
                        return  list;
                    }
                }catch (Exception e){
                    log.info(e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
    }

    //指定对应的id进行删除
    @Override
    public String deleteID(Integer id) {
        if (id == null) {
            log.info("传递过来的id是空的");
            throw new RuntimeException("传递过来的id是空的");
        }
        Boolean flag=activityMapper.deleteId(id);
        if(flag==true){
            return  "success";
        }else{
            return  "error";
        }
    }


    //根据id进行返回数据
    @Override
    public ActivityVO getListById(Integer id) {
        if (id == null) {
            log.info("传递过来的是空的");
            throw new RuntimeException("返回来的数据是空的");
        }
        ActivityVO activityVO=activityMapper.getIdListBy(id);
        if(activityVO==null){
            throw new RuntimeException("传递过来的数据是空的");
        }
        return activityVO;

    }

    //调用七牛云进行修改操作
    @Override
    public String editUrlImage(EditActivityImagesDTO editActivityImagesDTO, MultipartFile file) {
        if (editActivityImagesDTO == null) {
            log.info("传递过来的数据是空的");
            throw new RuntimeException("传递过来的数据是空的");
        }
        //产生的对应的地址
        String url=qiNiuUploader.editNewUploadUrl(editActivityImagesDTO.getUrl(),file,editActivityImagesDTO.getFolder());
        if (url==null){
            log.info("传递");
            throw new RuntimeException("传递过来的地址是不存在的");
        }else{
            return  url;
        }

    }

    //将数据库的地址进行修改
    @Override
    public String updateImageUrl(String url, Long id) {
        if (id == null || url == null) {
            log.info("传递过啊里的数据为空");
            throw new RuntimeException("传递过来的数据参数为空");
        }
        Boolean flag=activityMapper.updateImagesUrl(url,id);
        if(flag==true){
            return  "success";
        }else{
            return  "error";
        }

    }

    //编辑的实现逻辑
    @Override
    public String upDataListIdActivity(AddActivityDTO addActivityDTO) {
        if (addActivityDTO == null) {
            log.info("传递过来的是空的");
            throw new RuntimeException("传递过来的数据是空的");
        }
        AddActivity addActivity=new AddActivity();
        BeanUtils.copyProperties(addActivityDTO,addActivity);
        Boolean flag=activityMapper.upDataDataList(addActivity);
        if(flag==true){
            return  "success";
        }else{
            return  "error";
        }

    }
}
