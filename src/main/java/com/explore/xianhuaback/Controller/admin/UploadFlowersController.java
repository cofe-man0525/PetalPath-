package com.explore.xianhuaback.Controller.admin;

import com.explore.xianhuaback.DTO.AdminFlowersDTO.DeleteFlowersDTO;
import com.explore.xianhuaback.DTO.AdminFlowersDTO.EditFlowersUrlDTO;
import com.explore.xianhuaback.DTO.AdminFlowersDTO.EditListDTO;
import com.explore.xianhuaback.DTO.FlowersAddDTO;
import com.explore.xianhuaback.DTO.PageGetDTO;
import com.explore.xianhuaback.Entity.FlowersGetVO;
import com.explore.xianhuaback.Entity.PageResult;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.admin.FlowersService;
import com.explore.xianhuaback.Utils.QiNiuUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/Flowers")
public class UploadFlowersController {

    //获得对应的工具类
    @Autowired
    private QiNiuUploader  qiNiuUploader;

    @Autowired
    private FlowersService productService;

    //返回给前端url地址
    @PostMapping("/upload")
    public Result<Map<String,Object>> uploadFlowersImage(MultipartFile file){
        log.info("流程开始中");


        if (file.isEmpty()) {
            log.info("插入数据中");
            throw new RuntimeException("传递过来的图片为空");
        }

        String ImageUrl=qiNiuUploader.uploadImages(file);
        if(ImageUrl==null){
            throw new RuntimeException("没有返回图片的地址，查看工具栏");
        }else{
            Long flag=productService.insertImages(ImageUrl);
            if(flag==null){
                log.info("没有返回id");
                throw new RuntimeException("没有返回id");
            }else{

                HashMap<String,Object> map=new HashMap<>();
                if(flag==null){
                    throw new RuntimeException("传入数据库中失败");
                }else{
                    map.put("flag",flag);
                    map.put("imageUrl",ImageUrl);
                    return Result.success(map);
                }
            }
        }
    }

    //添加数据到数据库中
    @PostMapping("/addFlowers")
    public Result<String> addFlowers(@RequestBody FlowersAddDTO  flowersAddDTO){
        if(flowersAddDTO==null){
            log.info("传递参数开始");
            throw  new RuntimeException("传递过来的参数是空的");
        }
        if(flowersAddDTO.getId()==null){
            log.info("输入过来的id是空的");
            throw new RuntimeException("传递过来的id为空的");
        }
        try{
            String flag=productService.insertFlowersData(flowersAddDTO);
            if(flag==null){
                throw new RuntimeException("插入失败");
            }else{
                Result.success(flag);
            }
           return Result.success(flag);
        }catch (Exception e){
            log.error("Service层执行异常: ", e);
            throw new RuntimeException("处理失败: " + e.getMessage());
        }

    }

    //分页查询之后在返回去分页数据
    @PostMapping("/getList")
    public Result<PageResult<FlowersGetVO>> getListPagination(@RequestBody PageGetDTO pageGetDTO){
        log.info("开始进行传递参数的开始");
        if(pageGetDTO==null){
            log.info("传递过来的是空的");
            throw new RuntimeException("传递过来的是空的数据");
        }
        PageResult<FlowersGetVO> list=productService.getList(pageGetDTO);
        if (list==null){
            log.info("返回失败");
            throw new RuntimeException("返回过来的是空数据请查看逻辑层的数据");
        }
        return Result.success(list);

    }
    //指定Id进行删除
    @PostMapping("/delete")
    public Result<String> deleteFlowersById(@RequestBody DeleteFlowersDTO deleteFlowersDTO){
        if(deleteFlowersDTO.getId()==null||deleteFlowersDTO.getImageUrl()==null){
            log.info("传递参数开始");
            throw new RuntimeException("其中的传递过来的参数可能为空");
        }
        try{
            String flag=productService.deleteId(deleteFlowersDTO);
            if(flag==null){
                log.info("");
                throw new RuntimeException("验证的情况出现错误");
            }
            if(flag.equals("success")){
                log.info("传递的信号是对的");
                return Result.success(flag);
            }else{
                log.info("传递的信号是错误的");
                return Result.error(flag);
            }
        }catch (Exception e){
            log.info("已经存在问题");
            throw new RuntimeException("传递参数中出现问题了"+e.getMessage());
        }

    }

    //根据id来进行返回查询情况
    @GetMapping("/{id}")
    //这里传递的是返回对象的情况
    public Result<FlowersGetVO> getBySelectId(@PathVariable("id") Integer id){
        log.info("获得对应的id");
        if(id==null){
            log.info("id不存在");
            throw new RuntimeException("id不存在");
        }
        FlowersGetVO flowersGetVO=productService.getByIdList(id);
        if (flowersGetVO==null){
            throw new RuntimeException("传递过来的数据为空");
        }
        if(flowersGetVO.getId()==null){
            throw new RuntimeException("传递的数据有问题");}

        return Result.success(flowersGetVO);

    }

    //根据id来修改图片资源
    @PostMapping("/editUrl")
    public Result<String> editUploadUrl(
            @RequestParam("id") Long id,
            @RequestParam(value = "imageUrl", required = true) String imageUrl,
            @RequestParam("file") MultipartFile file
    ){
        EditFlowersUrlDTO  editFlowersUrlDTO=new EditFlowersUrlDTO();
        editFlowersUrlDTO.setId(id);
        editFlowersUrlDTO.setImageUrl(imageUrl);

        if(editFlowersUrlDTO==null){
            log.info("进行传递操作");
            throw new RuntimeException("参数可能为空的情况");
        }
        String ImageUrl=productService.editUploadUrl(editFlowersUrlDTO,file);
        if(ImageUrl==null){
            log.info("传递的参数可能为空数据");
            throw new RuntimeException("传递的数据为空");
        }else{

            return Result.success(ImageUrl);
        }


    }
    @PostMapping("/editList")
    public Result<String> editList(@RequestBody EditListDTO editLIstDTO){
        if(editLIstDTO==null){
            throw new RuntimeException("传递过来数据为空");
        }
        String flag=productService.editList(editLIstDTO);
        if(flag==null){
            throw new RuntimeException("逻辑层出现问题");
        }else{
            return Result.success(flag);
        }
    }




}


