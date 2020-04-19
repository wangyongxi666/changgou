package com.changgou.order.feign;

import com.changgou.entity.Result;
import com.changgou.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @ClassName AddressFegin
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月26日 19:53
 * @Version 1.0.0
*/
@FeignClient(name = "user")
public interface AddressFegin {

  @GetMapping("/address/list")
  public Result<List<Address>> list();
}
