package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.pay.config.RabbitMQConfig;
import com.changgou.pay.service.WXPayService;
import com.changgou.util.ConvertUtils;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WXPayController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月01日 1:08
 * @Version 1.0.0
*/
@RequestMapping("/wxpay")
@RestController
public class WXPayController {

  @Autowired
  private WXPayService wxPayService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @GetMapping("/nativePay")
  public Result nativePay(@RequestParam("orderId") String orderId,@RequestParam("money") Integer money){
    Map map = wxPayService.nativePay(orderId, money);
    return new Result(true, StatusCode.OK,"支付成功",map);
  }

  /**
   * @Author YongXi.Wang
   * @Description 支付成功回调
   * @Date 2020/3/1 12:32
   * @Param []
   * @return void
  **/
  @PostMapping("/notity")
  public void notity(HttpServletRequest request, HttpServletResponse response) {
    System.out.println("支付成功回调");

    try {
      //输入流转换成字符串
      ServletInputStream is = request.getInputStream();

      String xml = ConvertUtils.convertToString(is);
      System.out.println(xml);

      //查询微信的订单
      Map<String, String> stringStringMap = null;
      try {
        stringStringMap = WXPayUtil.xmlToMap(xml);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
      if("SUCCESS".equals(stringStringMap.get("result_code"))){
        //查询订单
        Map resultMap = wxPayService.queryOrder(stringStringMap.get("out_trade_no"));
        System.out.println("查询订单的结果:" + resultMap);

        if(resultMap != null){

          if("SUCCESS".equals(resultMap.get("result_code"))){

            //将订单消息发送到mq
            Map<String,String> mqMap = new HashMap<>();
            mqMap.put("orderId",(String) resultMap.get("out_trade_no"));
            mqMap.put("transactionId",(String) resultMap.get("transaction_id"));

            rabbitTemplate.convertAndSend("", RabbitMQConfig.ORDER_PAY, JSON.toJSONString(mqMap));

            //完成双向通信 发送消息到交换机 paynotify，触发跳转到支付成功页面(前端定义逻辑)
            rabbitTemplate.convertAndSend("paynotify","",resultMap.get("out_trade_no"));

            //给微信结果通知
            String responseStr = "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";

            response.setContentType("text/xml");
            response.getWriter().write(responseStr);

          }else{
            System.out.println(resultMap.get("err_code_des"));
          }
        }
      }else{
        System.out.println(stringStringMap.get("err_code_des"));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @Author YongXi.Wang
   * @Description  基于微信查询订单
   * @Date 2020/3/1 23:50
   * @Param
   * @return
  **/
  @GetMapping("/query/{orderId}")
  public Result queryOrder(@PathVariable("orderId") String orderId){
    Map map = wxPayService.queryOrder(orderId);
    return new Result(true,StatusCode.OK,"查询微信订单成功",map);
  }

  /**
   * @Author YongXi.Wang
   * @Description  基于微信关闭订单
   * @Date 2020/3/1 23:50
   * @Param
   * @return
  **/
  @PutMapping("/close/{orderId}")
  public Result closeOrder(@PathVariable("orderId") String orderId){
    Map map = wxPayService.closeyOrder(orderId);
    return new Result(true,StatusCode.OK,"关闭微信订单成功",map);
  }
}
