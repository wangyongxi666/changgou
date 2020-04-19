package com.changgou.seckill.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.seckill.config.TokenDecode;
import com.changgou.seckill.service.SecKillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SeckillOrderController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 19:36
 * @Version 1.0.0
*/
@RestController
@RequestMapping("/seckillorder")
public class SeckillOrderController {

  @Autowired
  private TokenDecode tokenDecode;

  @Autowired
  private SecKillOrderService secKillOrderService;

  /**
   * @Author YongXi.Wang
   * @Description 秒杀商品下单
   * @Date 2020/3/3 19:38
   * @Param [time, id]
   * @return com.changgou.entity.Result
  **/
  @RequestMapping("/add")
  public Result add(@RequestParam("time") String time,@RequestParam("id") Long id){

    //动态获取登陆人
    String username = tokenDecode.getUserInfo().get("username");

    //基于业务层实现秒杀下单
    boolean result = secKillOrderService.add(id, time, username);

    //返回结果
    if(result){
      return new Result(true, StatusCode.OK,"下单成功");
    }else{
      return new Result(false, StatusCode.ERROR,"下单失败");
    }

  }
}
