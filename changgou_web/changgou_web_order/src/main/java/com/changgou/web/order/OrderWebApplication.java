package com.changgou.web.order;

import com.changgou.interceptor.FeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName OrderWebApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月25日 17:19
 * @Version 1.0.0
*/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.order.feign","com.changgou.user.feign","com.changgou.pay.feign"})
public class OrderWebApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderWebApplication.class,args);
  }

  @Bean
  public FeignInterceptor feignInterceptor(){
    return new FeignInterceptor();
  }
}