package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ESManagerController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月21日 20:04
 * @Version 1.0.0
*/
@RestController
@RequestMapping("/manager")
public class ESManagerController {

  @Autowired
  private ESManagerService esManagerService;

  //创建索引结构
  @GetMapping("/create")
  public Result create(){
    esManagerService.createMappingAndIndex();
    return new Result(true, StatusCode.OK,"创建索引库成功");
  }

  //创建索引结构
  @GetMapping("/importAll")
  public Result importAll(){
    esManagerService.importAll();
    return new Result(true, StatusCode.OK,"创建索引结构成功");
  }


}
