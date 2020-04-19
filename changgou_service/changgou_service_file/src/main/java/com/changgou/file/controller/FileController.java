package com.changgou.file.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.file.util.FastDFSClient;
import com.changgou.file.util.FastDFSFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @ClassName FileController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月18日 12:49
 * @Version 1.0.0
*/
@RestController
@RequestMapping("/file")
public class FileController {


  @PostMapping("/upload")
  public Result uploadFile(MultipartFile file){

    try {

      //判断文件是否存在
      if(file == null){
        throw new RuntimeException("上传文件不能为空");
      }

      //获取文件全名
      String originalFilename = file.getOriginalFilename();
      if(originalFilename.isEmpty() || originalFilename == null){
        throw new RuntimeException("文件名称不能为空");
      }

      //获取文件后缀名
      String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

      //获取文件内容
      byte[] bytes = file.getBytes();

      //封装上传对象
      FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, bytes, substring );

      //调用工具类 上传文件
      String[] upload = FastDFSClient.upload(fastDFSFile);

      //封装返回结果
      String url = FastDFSClient.getTrackerUrl() + File.separator + upload[0] + upload[1];

      return new Result(true,StatusCode.OK,"文件上传成功",url);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return new Result(false, StatusCode.ERROR,"文件上传失败");
  }
}
