package com.changgou.oauth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @ClassName ApplyTokenTTest
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 12:43
 * @Version 1.0.0
*/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplyTokenTTest {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private LoadBalancerClient loadBalancerClient;

  @Test
  public void applyToken(){

    //构建请求地址  http://localhost:9200/oauth/token
    ServiceInstance serviceInstance = loadBalancerClient.choose("USER-AUTH");
    URI uri = serviceInstance.getUri();
    //申请令牌地址
    String url = uri + "/oauth/token";

    //封装请求参数
    MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
    body.add("grant_type","password");
    body.add("username","itheima");
    body.add("password","itheima");

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add("Authorization",this.getHttpBaic("changgou","changgou"));

    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(body, headers);

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
    //远程调用申请令牌
    ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
    Map result = exchange.getBody();
    System.out.println(result);
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
