package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.order.feign.OrderFegin;
import com.changgou.order.pojo.Order;
import com.changgou.pay.feign.WxPayFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @ClassName PayController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月01日 1:43
 * @Version 1.0.0
*/
@Controller
@RequestMapping("/wxpay")
public class PayController {

  @Autowired
  private OrderFegin orderFegin;

  @Autowired
  private WxPayFeign wxPayFeign;

  /**
   * @Author YongXi.Wang
   * @Description 跳转到微信支付二维码页面
   * @Date 2020/3/1 1:44
   * @Param [orderId]
   * @return java.lang.String
  **/
  @GetMapping
  public String wxPay(String orderId, Model model){

    //根据orderId查询订单，不存在则跳转到错误页面
    Result<Order> result = orderFegin.findById(orderId);
    Order order = result.getData();
    if(order == null){
      return "fail";
    }

    //该订单不是未支付状态也跳转到错误页面
    if(!"0".equals(order.getPayStatus())){
      return "fail";
    }

    //调用微信接口获取返回值
    Result resultPay = (Result) wxPayFeign.nativePay(orderId, order.getPayMoney());
    Map payMap = (Map) resultPay.getData();

    if(payMap == null){
      return "fail";
    }


    //封装数据
    payMap.put("orderId",orderId);
    payMap.put("payMoney",order.getPayMoney());

    model.addAllAttributes(payMap);

    return "wxpay";
  }

  //支付成功页面的跳转
  @RequestMapping("/toPaySuccess")
  public String toPaySuccess(Integer payMoney,Model model){
    model.addAttribute("payMoney",payMoney);
    return "paysuccess";
  }

}
