package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName CartController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月25日 13:42
 * @Version 1.0.0
*/
@RestController
@RequestMapping("/cart")
public class CartController {

  @Autowired
  private CartService cartService;

  @Autowired
  private TokenDecode tokenDecode;

  @PostMapping("/add")
  public Result addCart(@RequestParam("skuId")String skuId,@RequestParam("num") Integer num){

    //动态获取登陆人信息
//    String username = "itcast";
    String username = tokenDecode.getUserInfo().get("username");

    cartService.addCart(skuId,num,username);

    return new Result(true, StatusCode.OK,"加入购物车成功");
  }

  @GetMapping("/list")
  public Map list(){

    //动态获取登陆人信息
//    String username = "itcast";
    String username = tokenDecode.getUserInfo().get("username");

    Map map = cartService.list(username);

    return map;
  }
}
