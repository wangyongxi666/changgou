package com.changgou.system.filter;

import com.changgou.system.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @ClassName AuthorizeFilter
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月18日 21:25
 * @Version 1.0.0
*/
@Component
public class AuthorizeFilter implements GlobalFilter,Ordered {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    System.out.println("执行了登陆过滤器");

    //获取request
    ServerHttpRequest request = exchange.getRequest();

    //判断是否为登陆操作 则直接放行
    if(request.getURI().getPath().contains("/admin/login")){
      return chain.filter(exchange);
    }

    //获取response
    ServerHttpResponse response = exchange.getResponse();

    //获取token
    HttpHeaders headers = request.getHeaders();
    String token = headers.getFirst("token");

    if(token == null || token.isEmpty()){
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return response.setComplete();
    }

    //验证token
    try {
      JwtUtil.parseJWT(token);
    } catch (Exception e) {
      e.printStackTrace();
      //给前端返回错误信息
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return response.setComplete();
    }

    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return 1;
  }
}
