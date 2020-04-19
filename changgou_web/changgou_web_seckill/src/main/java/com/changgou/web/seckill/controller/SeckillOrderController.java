package com.changgou.web.seckill.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.seckill.feign.SecKillOrderFeign;
import com.changgou.util.CookieUtil;
import com.changgou.util.RandomUtil;
import com.changgou.web.seckill.aspect.AccessLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName SeckillOrderController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 13:54
 * @Version 1.0.0
*/
@RestController
@RequestMapping("/wseckillorder")
public class SeckillOrderController {

  @Autowired
  private RedisTemplate redisTemplate;

  @Autowired
  private SecKillOrderFeign secKillOrderFeign;

  @RequestMapping("/add")
  @AccessLimit
  public Result add(@RequestParam("time") String time,@RequestParam("id") Long id,
                    @RequestParam("random") String randomString, HttpServletRequest request){

    //检测传入的随机数是否正确
    String randomRedis = (String) redisTemplate.opsForValue().get("randomcode_" + this.getUidFromCookie(request));

    if(StringUtils.isEmpty(randomRedis)){
      return new Result(false, StatusCode.ERROR,"下单失败");
    }

    if(!randomRedis.equals(randomString)){
      return new Result(false, StatusCode.ERROR,"下单失败");
    }

    Result result = secKillOrderFeign.add(time, id);
    return result;
  }

  /**
   * 用于生成随机数返回前端，隐藏add方法
  **/
  @GetMapping("/getToken")
  public String getToken(HttpServletRequest request){

    //生成随机数
    String randomString = RandomUtil.getRandomString();

    //把随机数存入redis中，key为uid
    String uid = this.getUidFromCookie(request);

    redisTemplate.opsForValue().set("randomcode_"+uid,randomString,5, TimeUnit.SECONDS);

    System.out.println("随机字符串：" + randomString);

    return randomString;
  }

  //获取uid
  private String getUidFromCookie(HttpServletRequest request) {

    String uid = CookieUtil.readCookie(request, "uid").get("uid");

    Cookie[] cookies = request.getCookies();

    return uid;
  }


}
