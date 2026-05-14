package com.explore.xianhuaback.Service.admin.ServiceImpl;


import com.explore.xianhuaback.DTO.AdminFlowersDTO.DeleteFlowersDTO;
import com.explore.xianhuaback.DTO.AdminFlowersDTO.EditFlowersUrlDTO;
import com.explore.xianhuaback.DTO.AdminFlowersDTO.EditListDTO;
import com.explore.xianhuaback.DTO.FlowersAddDTO;
import com.explore.xianhuaback.DTO.PageGetDTO;
import com.explore.xianhuaback.Entity.AdminFlowers.DeleteFlowers;
import com.explore.xianhuaback.Entity.AdminFlowers.EditFlowersUrl;
import com.explore.xianhuaback.Entity.AdminFlowers.EditList;
import com.explore.xianhuaback.Entity.FlowersAdd;
import com.explore.xianhuaback.Entity.FlowersGetVO;
import com.explore.xianhuaback.Entity.PageGet;
import com.explore.xianhuaback.Entity.PageResult;
import com.explore.xianhuaback.Mapper.FlowersMapper;
import com.explore.xianhuaback.Service.admin.FlowersService;
import com.explore.xianhuaback.Utils.QiNiuUploader;
import com.qiniu.common.QiniuException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FlowersServiceImpl implements FlowersService {

    //设置的常量来控制数量
    private final static Integer STATUS = 1;

    //销售数量开始默认为0
    private final static Integer SALES_NUMBER=0;

    @Autowired
    private FlowersMapper flowersMapper;

    @Autowired
    private QiNiuUploader  qiNiuUploader;
    //将地址存到数据中
    @Override
    public Long insertImages(String imageUrl) {


        log.info("开始传递参数开始");
        if(imageUrl==null){
            throw new RuntimeException("传递过来为空值");
        }else{
           //首先先插入数据库中
            int result=flowersMapper.insertImageUrl(imageUrl); //插入到数据库中生成id
            if(result<=0){
                throw new RuntimeException("插入失败");
            }else {
                //如果插入成功在查询
                Long id = flowersMapper.getId(imageUrl);
                if (id == null) {
                    throw new RuntimeException("获取返回id失败");
                } else {
                    return id;
                }
            }
        }
    }

    //插入数据的情况
    @Override
    public String insertFlowersData(FlowersAddDTO flowersAddDTO) {
        //从接收的DTO中获取到id
        Integer id=flowersAddDTO.getId();
        if(id==null){
            throw new RuntimeException("传递过来的id为空");
        }
        if(flowersAddDTO.getGoodsItem()==null){
            log.info("传递过来的数据为空");
        }

          //创建出对应的
        FlowersAdd flowersAdd=new FlowersAdd();
        //进行赋值处理操作
        flowersAdd.setGoodsItem(flowersAddDTO.getGoodsItem());
        flowersAdd.setGoodsSort(flowersAddDTO.getGoodsSort());
        flowersAdd.setGoodsPrice(flowersAddDTO.getGoodsPrice());
        flowersAdd.setGoodsNumber(flowersAddDTO.getGoodsNumber());
        flowersAdd.setStatus(STATUS);
        flowersAdd.setSalesNumber(SALES_NUMBER);
        int flag=flowersMapper.insertFlowers(flowersAdd,id);
        if(flag<0){
            throw new RuntimeException("插入数据失败");
        }else{
            return "success";
        }
    }

    //分页查询
    @Override
    public PageResult<FlowersGetVO> getList(PageGetDTO pageGetDTO) {
        if(pageGetDTO==null){
            log.info("传递参数为:{}", pageGetDTO);
            throw new RuntimeException("传递过来的参数空");
        }
        PageGet  pageGet=new PageGet();
        BeanUtils.copyProperties(pageGetDTO,pageGet); //设置对应的实体类的传递参数形式
        //查询总条数
        Long Total=flowersMapper.getTotalList(pageGet);
        //新创建对象来控制参数变量
        if(Total==null){
            return new PageResult<>(List.of(), 0L,
                    pageGet.getPage(),
                    pageGet.getPageSize());
        }else{
            //查询当前的页的数据情况
            log.info("传递过来的参数是存在的");
            List<FlowersGetVO> records =flowersMapper.getSelectList(pageGet);
            PageResult<FlowersGetVO> pageResult = new PageResult<>(
                    records,
                    Total,
                    pageGet.getPage(),
                    pageGet.getPageSize()
            );
                    return pageResult;
        }
    }

    //指定对应的id进行删除
    @Override
    public String deleteId(DeleteFlowersDTO deleteFlowersDTO) throws QiniuException {
        log.info("传递参数开始");
        if(deleteFlowersDTO.getId()==null||deleteFlowersDTO.getImageUrl()==null){
            throw new RuntimeException("传递过来的数据两者可能为空");
        }
        //传递数据进行指定的删除
        DeleteFlowers deleteFlowers=new DeleteFlowers();
        BeanUtils.copyProperties(deleteFlowersDTO,deleteFlowers);
        FlowersGetVO flowersGetVO=flowersMapper.getListId(deleteFlowers.getId());
        if(flowersGetVO==null){
            log.info("请查看数据库检查方面");
            throw new RuntimeException("删除失败请查看数据库");
        }else{
                    Boolean flag1=flowersMapper.deleteId(deleteFlowers.getId());
                   if(flag1==false){
                       throw new RuntimeException("数据库删除失败");
                   }else{
                       qiNiuUploader.deleteQiNiuFile(deleteFlowers.getImageUrl());
                       return "success";
                   }

        }
    }

    //根据id来返回查询情况
    @Override
    public FlowersGetVO getByIdList(Integer id) {
        if(id==null){
            log.info("请查看接收层");
            throw new RuntimeException("传递过来的id时空的");
        }
        FlowersGetVO flowersGetVO=flowersMapper.getByListId(id);
        if(flowersGetVO==null){
            log.info("传递数据为空");
            throw new RuntimeException("传递的过来的数据为空");
        }
        return flowersGetVO;
    }

    //直接进行数据的修改操作
    @Override
    public String editUploadUrl(EditFlowersUrlDTO editFlowersUrlDTO, MultipartFile file) {
        if(editFlowersUrlDTO==null){
            log.info("传递过来的参数是空的");
            throw new RuntimeException("传递过来的参数是空的");
        }
        EditFlowersUrl editFlowersUrl=new EditFlowersUrl();
        BeanUtils.copyProperties(editFlowersUrlDTO,editFlowersUrl);
        String url=qiNiuUploader.editUploadUrl(editFlowersUrl.getImageUrl(),file);
        if(url==null){
            log.info("添加成功");
            throw new RuntimeException("删除失败，和添加失败情况");
        }else{
            //一种是更新掉数据库原来的url地址情况
            Boolean flag=flowersMapper.editUploadImageUrl(editFlowersUrl.getId(),url);
            if(flag==false){
                throw new RuntimeException("数据层修改失败");
            }else{
                //返回新的图片的地址
                log.info("传递的数据情况成功"+flag);
                return url;
            }
        }
    }

    //将剩下的数据进行编辑
    @Override
    public String editList(EditListDTO editListDTO) {
        EditList  editList=new EditList();
        BeanUtils.copyProperties(editListDTO,editList);
        editList.setStatus(STATUS);
        editList.setSalesNumber(SALES_NUMBER);
        editList.setCreatedTime(LocalDateTime.now());
        Boolean flag=flowersMapper.editList(editList);
        if(flag==false){
            throw new RuntimeException("出现问题解决");
        }else{
            return "success";
        }
    }
}
