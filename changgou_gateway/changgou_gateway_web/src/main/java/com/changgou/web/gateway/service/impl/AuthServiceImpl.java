package com.changgou.web.gateway.service.impl;

import com.changgou.web.gateway.service
        .AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * @ClassName AuthServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 22:09
 * @Version 1.0.0
*/
@Service
public class AuthServiceImpl implements AuthService{
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Override
  public String getJtiFromCookie(ServerHttpRequest request) {
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
    HttpCookie uid = cookies.getFirst("uid");
    if(uid != null){
      String value = uid.getValue();
      return value;
    }
    return null;
  }

  @Override
  public String getJwtFromRedis(String jti) {

    String jwt = stringRedisTemplate.boundValueOps(jti).get();
    if(StringUtils.isEmpty(jwt)){
      return "";
    }

    return jwt;
  }
}
