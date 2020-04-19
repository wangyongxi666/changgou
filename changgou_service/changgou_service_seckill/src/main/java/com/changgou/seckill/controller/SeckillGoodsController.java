package com.changgou.seckill.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SecKillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName SeckillGoodsController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 12:23
 * @Version 1.0.0
*/
@RestController
@RequestMapping("/seckillgoods")
public class SeckillGoodsController {

  @Autowired
  private SecKillGoodsService secKillGoodsService;

  @GetMapping("/list")
   public Result list(@RequestParam("time") String time){

    List<SeckillGoods> list = secKillGoodsService.list(time);

    return new Result(true, StatusCode.OK,"查询成功",list);

  }

}
