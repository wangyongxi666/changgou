package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.feign.CartFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName CartController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月25日 19:26
 * @Version 1.0.0
*/
@Controller
@RequestMapping("/wcart")
public class CartController {

  @Autowired
  private CartFeign cartFeign;

  //查询
  @GetMapping("/list")
  public String list(Model model){

    Map map = cartFeign.list();
    model.addAttribute("items",map);

    return "cart";
  }

  //添加
  @PostMapping("/add")
  @ResponseBody
  public Result<Map> add(String id,Integer num){

    cartFeign.addCart(id, num);

    Map map = cartFeign.list();

    return new Result<>(true, StatusCode.OK,"添加购物车成功",map);
  }

  //添加
  @GetMapping("/add")
  public String addForGet(String id,Integer num,Model model){

    cartFeign.addCart(id, num);
    Map map = cartFeign.list();

    model.addAttribute("items",map);

    return "cart";


  }

}
