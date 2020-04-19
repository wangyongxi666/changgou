package com.changgou.system.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * @ClassName IpFilter
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月18日 18:16
 * @Version 1.0.0
*/
//@Component
public class IpFilter implements GlobalFilter, Ordered{

  /**
   * 具体业务逻辑
  **/
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    System.out.println("第一个过滤器");

    ServerHttpRequest request = exchange.getRequest();
    InetSocketAddress remoteAddress = request.getRemoteAddress();
    System.out.println("ip:" + remoteAddress.getHostName());

    //放行

    return chain.filter(exchange);
  }

  /**
   * 过滤器优先级 返回值越小 优先级越高
  **/
  @Override
  public int getOrder() {
    return 1;
  }
}
