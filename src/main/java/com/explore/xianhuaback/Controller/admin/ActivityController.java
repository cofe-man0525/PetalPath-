package com.explore.xianhuaback.Controller.admin;
import com.explore.xianhuaback.DTO.AdminActivityDTO.ActivityInputSelectDTO;
import com.explore.xianhuaback.DTO.AdminActivityDTO.AddActivityDTO;
import com.explore.xianhuaback.DTO.AdminActivityDTO.EditActivityImagesDTO;
import com.explore.xianhuaback.Entity.AdminAddActivity.ActivityVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.admin.ActivityService;
import com.explore.xianhuaback.Utils.QiNiuUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/activity")
public class ActivityController  {

    @Autowired
    private ActivityService  activityService;

    @Autowired
    private QiNiuUploader qiNiuUploader;

    //图片上传
    @PostMapping("/upload")
    public Result<Map<String,Object>> uploadImage(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("folder") String folder) {

        log.info("传递数据开始");
        if (file.isEmpty()) {
            throw new RuntimeException("传递过来的是空数据请查看前端查看数据");
        }
        try{
            //上传七牛云的图片栏目
            String ImageUrl=qiNiuUploader.uploadActivityImages(file,folder);
            if(ImageUrl==null){
                log.info("返回来的数据是空的");
                throw new RuntimeException("传递过来的url地址是空的");
            }else{
                Long id=activityService.insertImagesUrl(ImageUrl);
                if(id==null){
                    log.info("传递过来的id是空的");
                    throw new RuntimeException("传递过来的id是空的");
                }else{
                    log.info("");
                    Map<String,Object> map=new HashMap<>();
                    map.put("flag",id);
                    map.put("imageUrl",ImageUrl);
                    return Result.success(map);
                }
            }

        }catch (Exception e){
            log.info("出现了传递错误情况");
            throw new RuntimeException("传递失败");
        }
    }

    @PostMapping("/addActivity")
    public Result<String> addActivity(@RequestBody AddActivityDTO  addActivityDTO) {
        if(addActivityDTO==null){
            log.info("传递过来的数据是空的");
            throw new RuntimeException("传递过来的数据是空的");
        }

        //尝试的对应情况
        try{
            //尝试的对应的数据
            String flag=activityService.IdinsertList(addActivityDTO);
            if (flag == null || flag.isEmpty()) {
                log.error("逻辑层返回null或空字符串");
                return Result.error("活动保存失败");
            }

            if(flag.equals("error")){
                log.info("逻辑层出现了问题");
                throw new RuntimeException("错误出现在逻辑层");
            }else{
                return Result.success(flag);
            }
        }catch (Exception e){
            log.info("出现了错误");
            throw new RuntimeException("运行出现了错误");
        }
    }

    @GetMapping("/getList")
    public Result<List<ActivityVO>> getByIdList(){
        try{
            log.info("获得对应的数据列表形式");
            List<ActivityVO>  ActivityVO=activityService.getByIdList();
            if (ActivityVO==null){
                throw new RuntimeException("传递返回来过来的数据是空的");
            }

            return Result.success(ActivityVO);
        }catch (Exception e){
            log.info("回显出现了问题");
            throw new RuntimeException(e);
        }

    }

    //条件搜索的筛选功能
    @PostMapping("/Input")
    public Result<List<ActivityVO>>  SelectInputList(@RequestBody ActivityInputSelectDTO activityInputSelectDTO){
        log.info("传递过来的输入名字为："+activityInputSelectDTO.getTopInput()+"状态为:"+activityInputSelectDTO.getStatus());
        List<ActivityVO> list=activityService.SelectInputList(activityInputSelectDTO);
        if (list==null){
            log.info("返回来的数据是空的");
            throw new RuntimeException("返回来的数据是空的");
        }
        return Result.success(list);
    }

    //删除对应的数据情况
    @DeleteMapping("/delete")
    public Result<String> deleteActivityId(@RequestParam("id") Integer id){
        log.info("传递过来的id了");
        if(id==null){
            throw new RuntimeException("传递过来的id是空的");
        }
        String flag=activityService.deleteID(id);
        if(flag.equals("error")){
            log.info("error");
            throw new RuntimeException("删除失败");
        }else{
            return Result.success(flag);
        }
    }



    @PostMapping("/getEditId")
    public Result<ActivityVO> getById(@RequestParam("id") Integer id){
            log.info("获得对应的id为："+id);
            if(id==null){
                throw new RuntimeException("传递过来的数据是空的");
            }
        ActivityVO activityVO=activityService.getListById(id);
            if(activityVO==null){
                log.info("返回来的数据是空的");
                throw new RuntimeException("返回过来的数据是空的");
            }
            return Result.success(activityVO);
    }



    //在编辑修改中修改图片上传
    @PostMapping("/editImages")
    public Result<Map<String,Object>> editImagesUpload(
             @RequestParam("folder") String folder,
             @RequestParam("id") Long id,
             @RequestParam(value = "imageUrl", required = true) String imageUrl,
             @RequestParam("file") MultipartFile file)
    {
            log.info(folder); //传递过来的数据形式
            if(file == null || file.isEmpty()){
                log.info("传递过来的数据是空的");
                throw new RuntimeException("传递过来的数据是空的");
            }

             EditActivityImagesDTO editActivityImagesDTO=new EditActivityImagesDTO();
             editActivityImagesDTO.setId(id);
             editActivityImagesDTO.setFolder(folder);
             editActivityImagesDTO.setUrl(imageUrl);

             //从这里进行返回来七牛云的地址进行返回接收在进行对数据库的修改操作存储具体在逻辑层的水平
            String url=activityService.editUrlImage(editActivityImagesDTO,file);
            if(url==null){
                log.info("传递过来的地址不存在");
                throw new RuntimeException("地址不存在");
            }else{
                String flag=activityService.updateImageUrl(url,editActivityImagesDTO.getId());
                if(flag.equals("error")){
                    log.info("error");
                    throw new RuntimeException("运行失误");
                }else{

                    //进行返回集合的框架
                    Map<String,Object> map=new HashMap<>();
                    map.put("flag",editActivityImagesDTO.getId());
                    map.put("imageUrl",imageUrl);
                    return Result.success(map);
                }
            }
    }
    @PostMapping("/edit")
    public Result<String> editDataList(@RequestBody AddActivityDTO addActivityDTO){
        if(addActivityDTO==null){
            log.info("传递过来的数据为空");
            throw new RuntimeException("传递过来的数据为空");
        }
        String flag=activityService.upDataListIdActivity(addActivityDTO);
        if(flag.equals("error")){
            return Result.error("添加失败");
        }else{
            log.info("成功的修改");
            return Result.success(flag);
        }

    }






}
