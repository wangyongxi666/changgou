package com.changgou.pay.service.impl;

import com.changgou.pay.service.WXPayService;
import com.github.wxpay.sdk.WXPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WXPayServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月01日 0:54
 * @Version 1.0.0
*/
@Service
public class WXPayServiceImpl implements WXPayService{

  @Autowired
  private WXPay wxPay;

  @Value("${wxpay.notity_url}")
  private String notity_url;

  @Override
  public Map nativePay(String orderId, Integer money) {

    try {
      //封装请求参数
      Map requestMap = new HashMap();

      requestMap.put("body","畅购");
      requestMap.put("out_trade_no",orderId);

      //用于测试 一分钱
      BigDecimal payMoney = new BigDecimal("0.01");
      BigDecimal fen = payMoney.multiply(new BigDecimal(100));
      fen = fen.setScale(0, BigDecimal.ROUND_UP);
      requestMap.put("total_fee",String.valueOf(fen));

      requestMap.put("spbill_create_ip","127.0.0.1");
      requestMap.put("notify_url",notity_url);
      requestMap.put("trade_type","NATIVE");

      //基于wxpay调用接口并返回参数
      Map map = wxPay.unifiedOrder(requestMap);

      return map;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public Map queryOrder(String orderId) {

    Map<String,String> map = new HashMap<>();
    map.put("out_trade_no",orderId);

    try {

      Map<String, String> stringStringMap = wxPay.orderQuery(map);
      return stringStringMap;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Map closeyOrder(String orderId) {

    try {

      Map<String,String> map = new HashMap();
      map.put("out_trade_no",orderId);
      Map<String, String> stringStringMap = wxPay.closeOrder(map);

      return stringStringMap;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

}
