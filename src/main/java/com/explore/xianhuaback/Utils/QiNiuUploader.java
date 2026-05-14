package com.explore.xianhuaback.Utils;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.http.Response;
import com.explore.xianhuaback.Config.QiNiuConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@Slf4j
public class QiNiuUploader {

    @Autowired
    private QiNiuConfig qiNiuConfig;

    //通过前端来传递给后端来上传到平台在返回给url地址
    //针对于两个参数形式得知一个为文件和分类情况
    //在七牛云的文件夹florwsGoods
    public String uploadImages(MultipartFile file){
        //获得文件的原始名
        try{
            log.info("开始进行使用工具类的情况来传递过来");

            // 1. 获取文件的原始名称
            String originalFilename = file.getOriginalFilename();

            // 2. 获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 3. 生成唯一文件名：分类/年月/随机数.后缀
            String fileName = "florwsGoods" + "/" +
                    System.currentTimeMillis() + "_" +
                    UUID.randomUUID().toString().replace("-", "") +
                    suffix;

            //创建出针对于配置上传的环境
            // 4. 创建七牛云配置 ,添加依赖
            Configuration cfg = new Configuration(Region.autoRegion());
            UploadManager uploadManager = new UploadManager(cfg);

            // 5. 获取上传凭证
            Auth auth = Auth.create(qiNiuConfig.getAccessKey(), qiNiuConfig.getSecretKey());
            String upToken = auth.uploadToken(qiNiuConfig.getBucket());

            //判断中是否上传成功
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            if(response.isOK()){
                //上传成功就会返回url地址给前端重新修改的文件+地址
                String url = qiNiuConfig.getDomain() + "/" + fileName;
                log.info("上传成功: {}", url);
                return url;  // ✅ 成功返回URL
            }else{
                log.error("上传失败，状态码: {}", response.statusCode);
                return null;  // ❌ 失败返回null
            }

        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    //Activity活动的情况的加载
    public String uploadActivityImages(MultipartFile file, String folder){
        //获得文件的原始名
        try{
            log.info("开始进行使用工具类的情况来传递过来");

            // 1. 获取文件的原始名称
            String originalFilename = file.getOriginalFilename();

            // 2. 获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 3. 生成唯一文件名：分类/年月/随机数.后缀
            String fileName = folder + "/" +
                    System.currentTimeMillis() + "_" +
                    UUID.randomUUID().toString().replace("-", "") +
                    suffix;

            //创建出针对于配置上传的环境
            // 4. 创建七牛云配置 ,添加依赖
            Configuration cfg = new Configuration(Region.autoRegion());
            UploadManager uploadManager = new UploadManager(cfg);

            // 5. 获取上传凭证
            Auth auth = Auth.create(qiNiuConfig.getAccessKey(), qiNiuConfig.getSecretKey());
            String upToken = auth.uploadToken(qiNiuConfig.getBucket());

            //判断中是否上传成功
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            if(response.isOK()){
                //上传成功就会返回url地址给前端重新修改的文件+地址
                String url = qiNiuConfig.getDomain() + "/" + fileName;
                log.info("上传成功: {}", url);
                return url;  // ✅ 成功返回URL
            }else{
                log.error("上传失败，状态码: {}", response.statusCode);
                return null;  // ❌ 失败返回null
            }

        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    private String getKeyFromUrl(String imageUrl){
        if(imageUrl==null|| imageUrl.isEmpty()){
            log.info("获得对应的地址错误");
            throw new RuntimeException("地址获取错误");
        }
        // 去掉域名部分，只保留路径
        return imageUrl.replace(qiNiuConfig.getDomain() + "/", "");
    }

    //删除对应的七牛云平台的图片资源
    public Boolean deleteQiNiuFile(String filePath) throws QiniuException {
        //提取key,获得纯的路径
        String imageUrlKey=getKeyFromUrl(filePath);
        if(imageUrlKey.isEmpty()){
            log.info("获得地址不存在");
            throw new RuntimeException("获得地址不存在");
        }

        //创建认证信息
        Auth auth = Auth.create(qiNiuConfig.getAccessKey(), qiNiuConfig.getSecretKey());

        // 3. 创建BucketManager对象（用于管理空间文件）
        Configuration cfg = new Configuration(Region.autoRegion());
        BucketManager bucketManager = new BucketManager(auth, cfg);

        Response response = bucketManager.delete(qiNiuConfig.getBucket(), imageUrlKey);
        if (response.isOK()) {
            log.info("七牛云图片删除成功: {}", imageUrlKey);
            return true;
        } else {
            log.error("七牛云图片删除失败: {}, 状态码: {}", imageUrlKey);
            return false;
        }
    }

    //删除旧的指定id的地址新加最新的图片地址，然后返回新的图片的地址情况
    public String editUploadUrl(String imageUrl, MultipartFile file) {

        try{
            if(imageUrl==null|| imageUrl.isEmpty()){
                log.info("传递的参数可能为空");
                throw new RuntimeException("两者其中之一会有空值");
            }
            //首先删除指定的图片的地址
            Boolean flag=deleteQiNiuFile(imageUrl);
            if(flag==false){
                log.info("删除失败的情况");
                throw new RuntimeException("删除失败");
            }else{
                //在添加上新的图片然后进行返回新图片的地址操作
                String newUrlImage=uploadImages(file);
                if(newUrlImage==null){
                    log.info("编辑之后产生的新的图片失败");
                    throw new RuntimeException("上传图片中失败");
                }else{
                    return newUrlImage;
                }
            }
        }catch (Exception e){
            log.info("出现修改错误");
           throw new RuntimeException(e);
        }


    }

    //删除旧的指定id的地址新加最新的图片地址，然后返回新的图片的地址情况
    public String editNewUploadUrl(String imageUrl, MultipartFile file,String folder) {

        try{
            if(imageUrl==null|| imageUrl.isEmpty()){
                log.info("传递的参数可能为空");
                throw new RuntimeException("两者其中之一会有空值");
            }
            //首先删除指定的图片的地址
            Boolean flag=deleteQiNiuFile(imageUrl);
            if(flag==false){
                log.info("删除失败的情况");
                throw new RuntimeException("删除失败");
            }else{
                //在添加上新的图片然后进行返回新图片的地址操作
                String newUrlImage=uploadActivityImages(file, folder);
                if(newUrlImage==null){
                    log.info("编辑之后产生的新的图片失败");
                    throw new RuntimeException("上传图片中失败");
                }else{
                    return newUrlImage;
                }
            }

        }catch (Exception e){
            log.info("出现修改错误");
            throw new RuntimeException(e);
        }


    }
}
