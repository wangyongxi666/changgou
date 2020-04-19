package com.changgou.web.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName WebGateWayApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 21:41
 * @Version 1.0.0
*/
@SpringBootApplication
@EnableDiscoveryClient
public class WebGateWayApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebGateWayApplication.class,args);
  }
}