package com.explore.xianhuaback.Controller.admin;

import com.explore.xianhuaback.DTO.AdminPoint.AdminPointDTO;
import com.explore.xianhuaback.DTO.AdminPoint.EditPointsImagesDTO;
import com.explore.xianhuaback.Entity.AdminPoints.AdminPoints;
import com.explore.xianhuaback.Entity.AdminPoints.AdminPointsVO;
import com.explore.xianhuaback.Result.Result;
import com.explore.xianhuaback.Service.admin.PointsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/Points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    // 图片上传
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {
        log.info("传递过来的数据情况");
        if (file.isEmpty() || file == null) {
            log.info("开始进行传递参数");
            throw new RuntimeException("传递过来的参数可能为空");
        }

        // 开始进行接入数据库将地址存入到数据库
        String url = pointsService.uploadImages(file, folder);
        if (url == null) {
            log.info("传递过来的数据是空的");
            throw new RuntimeException("传递过来的地址是空的");
        } else {

            // 再将这个url传递给后端再返回来id进行展示
            Long id = pointsService.getByIdImages(url);
            if (id == null) {
                log.info("返回来的id是空的");
                throw new RuntimeException("返回来的id不存在");

            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("url", url);
                return Result.success(map);
            }

        }

    }

    // 获取所有的数据情况
    @GetMapping("/getList")
    public Result<List<AdminPointsVO>> getList() {
        log.info("进行查询数据的情况");
        List<AdminPointsVO> AdminPointsVO = pointsService.getList();
        if (AdminPointsVO == null) {
            log.info("传递失败");
            throw new RuntimeException("返回来的数据不存在请查看代码");
        } else {
            AdminPointsVO.forEach(item -> {
                if (item.getId() == null) {
                    item.setImageUrl("默认图片"); // 修改 null 为默认图片
                }
            });
            return Result.success(AdminPointsVO);
        }

    }

    // 将剩下的数据进行返回
    @PostMapping("/add")
    public Result<String> add(@RequestBody AdminPointDTO adminPointDTO) {
        log.info(String.valueOf(adminPointDTO.getStatus()));
        if (adminPointDTO == null) {
            log.info("传递过来的数据是不存在的");
            throw new RuntimeException("传递过来的数据是空的");
        } else {
            String flag = pointsService.addDataPoints(adminPointDTO);
            if (flag == null) {
                throw new RuntimeException("循环中又存在问题");
            }
            if (flag.equals("success")) {
                return Result.success("success");

            } else {
                return Result.error("error");
            }
        }
    }

    @PostMapping("/edit")
    public Result<String> edit(@RequestBody AdminPointDTO adminPointDTO) {
        log.info(String.valueOf(adminPointDTO != null ? adminPointDTO.getStatus() : null));
        if (adminPointDTO == null) {
            log.info("传递过来的数据是不存在的");
            throw new RuntimeException("传递过来的数据是空的");
        }
        String flag = pointsService.editDataPoints(adminPointDTO);
        if (flag == null) {
            throw new RuntimeException("循环中又存在问题");
        }
        if (flag.equals("success")) {
            return Result.success("success");
        }
        return Result.error("error");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {

        if (id == null) {
            log.info("传递过来的id是不存在的");
            throw new RuntimeException("传递过啊里的数据是空的");
        }
        String flag = pointsService.deleteId(id);
        if (flag.equals("success")) {
            return Result.success("success");
        } else {
            return Result.error("error");
        }
    }

    @GetMapping("/search")
    public Result<List<AdminPointsVO>> search(
            @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("积分搜索，keyword={}", keyword);
        List<AdminPointsVO> list = pointsService.searchByKeyword(keyword);
        if (list == null) {
            log.info("传递失败");
            throw new RuntimeException("返回来的数据不存在请查看代码");
        }
        list.forEach(item -> {
            if (item.getId() == null) {
                item.setImageUrl("默认图片");
            }
        });
        return Result.success(list);
    }

    @GetMapping("/getById")
    public Result<List<AdminPointsVO>> getById(@RequestParam Long id) {
        log.info("根据 id 查询积分详情，id={}", id);
        List<AdminPointsVO> list = pointsService.getById(id);
        if (list == null) {
            log.info("传递失败");
            throw new RuntimeException("返回来的数据不存在请查看代码");
        }
        list.forEach(item -> {
            if (item.getId() == null) {
                item.setImageUrl("默认图片");
            }
        });
        return Result.success(list);
    }

    @PostMapping("/editUpload")
    public Result<Map<String, Object>> editUpload(
            @RequestParam("folder") String folder,
            @RequestParam("id") Long id,
            @RequestParam(value = "oldImageUrl", required = true) String oldImageUrl,
            @RequestParam("file") MultipartFile file) {
        log.info(folder);
        if (file == null || file.isEmpty()) {
            log.info("传递过来的数据是空的");
            throw new RuntimeException("传递过来的数据是空的");
        }
        EditPointsImagesDTO dto = new EditPointsImagesDTO();
        dto.setId(id);
        dto.setFolder(folder);
        dto.setUrl(oldImageUrl);

        String url = pointsService.editUrlImage(dto, file);
        if (url == null) {
            log.info("传递过来的地址不存在");
            throw new RuntimeException("地址不存在");
        }
        String flag = pointsService.updateImageUrl(url, dto.getId());
        if (flag.equals("error")) {
            log.info("error");
            throw new RuntimeException("运行失误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("flag", dto.getId());
        map.put("imageUrl", oldImageUrl);
        return Result.success(map);
    }

}
