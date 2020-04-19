package com.changgou.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.changgou.entity.Result;

@FeignClient("pay")
public interface WxPayFeign {

  /**
   * 下单
   * @param orderId
   * @param money
   * @return
   */
  @GetMapping("/wxpay/nativePay")
  public Result nativePay(@RequestParam("orderId") String orderId,
                          @RequestParam("money") Integer money );

  //查询微信订单
  @GetMapping("/wxpay/query/{orderId}")
  public Result queryOrder(@PathVariable("orderId") String orderId);

  //关闭微信订单
  @PutMapping("/wxpay/close/{orderId}")
  public Result closeOrder(@PathVariable("orderId") String orderId);
}
