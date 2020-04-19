package com.changgou.system.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName UrlFilter
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月18日 18:21
 * @Version 1.0.0
*/
//@Component
public class UrlFilter implements GlobalFilter, Ordered {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    System.out.println("第二个过滤器");

    ServerHttpRequest request = exchange.getRequest();

    String path = request.getURI().getPath();

    System.out.println("path :" + path);

    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return 2;
  }
}
