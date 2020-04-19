package com.changgou.web.gateway.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface AuthService {
  String getJtiFromCookie(ServerHttpRequest request);

  String getJwtFromRedis(String jti);
}
