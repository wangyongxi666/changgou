package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AuthServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 14:23
 * @Version 1.0.0
*/
@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private LoadBalancerClient loadBalancerClient;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Value("${auth.ttl}")
  private long ttl;


  @Override
  public AuthToken login(String username, String password, String clientId, String clientSecret) {

    //申请令牌
    ServiceInstance choose = loadBalancerClient.choose("USER-AUTH");
    String url = choose.getUri() + "/oauth/token";

    MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
    body.add("grant_type","password");
    body.add("username",username);
    body.add("password",password);

    MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
    headers.add("Authorization",this.getHttpBaic("changgou","changgou"));

    HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(body,headers);

    //当后端出现 401，400 ，后端不对他进行处理，直接返回前端
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public void handleError(ClientHttpResponse response) throws IOException {
        //当响应的值为400或401时候也要正常响应，不要抛出异常
        if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
          super.handleError(response);
        }
      }
    });

    //发送请求
    ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

    //封装结果数据
    Map map = exchange.getBody();
    if(map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null){
      //申请令牌失败
      throw new RuntimeException("申请令牌失败");
    }
    AuthToken authToken = new AuthToken();
    authToken.setAccessToken((String)map.get("access_token"));
    authToken.setRefreshToken((String)map.get("refresh_token"));
    authToken.setJti((String)map.get("jti"));

    //jti作为redis 的key，jwt当作redis中的value存储
    stringRedisTemplate.boundValueOps(authToken.getJti()).set(authToken.getAccessToken(),ttl, TimeUnit.SECONDS);

    return authToken;
  }

  //生成  Authorization
  private String getHttpBaic(String clientId, String ClientSecret) {

    //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
    String string = clientId+":"+ClientSecret;

    //进行base64编码
    byte[] encode = Base64Utils.encode(string.getBytes());

    return "Basic "+new String(encode);
  }

}
