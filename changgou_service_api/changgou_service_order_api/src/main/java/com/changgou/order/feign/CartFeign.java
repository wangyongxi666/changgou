package com.changgou.order.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName CartFeign
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月25日 18:32
 * @Version 1.0.0
*/
@FeignClient(name = "order")
public interface CartFeign {

  @PostMapping("/cart/add")
  public Result addCart(@RequestParam("skuId")String skuId, @RequestParam("num") Integer num);

  @GetMapping("/cart/list")
  public Map list();

}
