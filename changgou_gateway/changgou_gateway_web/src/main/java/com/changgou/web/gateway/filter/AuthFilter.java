package com.changgou.web.gateway.filter;

import com.changgou.web.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @ClassName AuthFilter
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 22:04
 * @Version 1.0.0
*/
@Component
public class AuthFilter implements GlobalFilter,Ordered{

  @Autowired
  private AuthService authService;

  private static String LOGIN_URL = "http://localhost:8001/api/oauth/toLogin";

  //过滤逻辑
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    //放行登陆请求
    String path = request.getURI().getPath();
    if(path.contains("api/oauth/login") || !UrlFilter.hasAuthorize(path)){
      return chain.filter(exchange);
    }

    //从cookie中获取jti，如果不存在，则跳转登陆页面
    String jti = authService.getJtiFromCookie(request);
    if(StringUtils.isEmpty(jti)){
//      response.setStatusCode(HttpStatus.UNAUTHORIZED);
//      return response.setComplete();

      //跳转登陆页面
      return this.toLoginPage(LOGIN_URL+"?FROM="+request.getURI(),exchange);
    }

    //从redis获取jwt的值，不存在，则跳转登陆页面
    String jwt = authService.getJwtFromRedis(jti);
    if(StringUtils.isEmpty(jwt)){
//      response.setStatusCode(HttpStatus.UNAUTHORIZED);
//      return response.setComplete();

      //跳转登陆页面
      return this.toLoginPage(LOGIN_URL+"?FROM="+request.getURI(),exchange);
    }

    //对当前对象进行增强，让他携带jwt
    request.mutate().header("Authorization","Bearer" + jwt);

    return chain.filter(exchange);
  }

  //跳转登陆页面方法
  private Mono<Void> toLoginPage(String loginUrl, ServerWebExchange exchange) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.SEE_OTHER);//跳转代码
    response.getHeaders().set("Location",loginUrl);
    return response.setComplete();
  }

  //执行优先级
  @Override
  public int getOrder() {
    return 1;
  }
}
