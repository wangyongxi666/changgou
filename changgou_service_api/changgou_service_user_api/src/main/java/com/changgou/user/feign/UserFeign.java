package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName UserFeign
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 18:40
 * @Version 1.0.0
*/
@FeignClient(name = "user")
public interface UserFeign {

  @GetMapping("/user/load/{username}")
  public User findUserInfo(@PathVariable("username") String username);

}
