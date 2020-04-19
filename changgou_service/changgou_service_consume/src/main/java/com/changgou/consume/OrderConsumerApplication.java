package com.changgou.consume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName OrderConsumerApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月04日 1:20
 * @Version 1.0.0
*/
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.changgou.consume.dao"})
public class OrderConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderConsumerApplication.class,args);
  }
}