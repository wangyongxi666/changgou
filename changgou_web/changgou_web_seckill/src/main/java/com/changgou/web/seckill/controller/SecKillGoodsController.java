package com.changgou.web.seckill.controller;

import com.changgou.entity.Result;
import com.changgou.seckill.feign.SecKillGoodsFeign;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SecKillGoodsController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 1:44
 * @Version 1.0.0
*/
@Controller
@RequestMapping("/wseckillgoods")
public class SecKillGoodsController {

  @Autowired
  private SecKillGoodsFeign secKillGoodsFeign;

  //跳转秒杀首页
  @RequestMapping("/toIndex")
  public String toIndex(){
    return "seckill-index";
  }

  //获取秒杀时间段商品信息
  @RequestMapping("/timeMenus")
  @ResponseBody
  public List<String> dateMenus(){
    List<Date> dateMenus = DateUtil.getDateMenus();

    List<String> list = new ArrayList<>();

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    for (Date dateMenu : dateMenus) {
      list.add(format.format(dateMenu));
    }

    return list;
  }

  @RequestMapping("/list")
  @ResponseBody
  public Result<List<SeckillGoods>> list(@RequestParam("time") String time){

    Result list = secKillGoodsFeign.list(time);

    return list;

  }

}
