package com.changgou.seckill.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName SecKillGoodsFeign
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 12:31
 * @Version 1.0.0
*/
@FeignClient(name = "seckill")
public interface SecKillGoodsFeign {

  @GetMapping("/seckillgoods/list")
  public Result list(@RequestParam("time") String time);

}
