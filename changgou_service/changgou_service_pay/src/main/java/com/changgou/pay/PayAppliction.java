package com.changgou.pay;

import com.github.wxpay.sdk.MyConfig;
import com.github.wxpay.sdk.WXPay;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName PayAppliction
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月01日 0:45
 * @Version 1.0.0
*/
@SpringBootApplication
@EnableEurekaClient
public class PayAppliction {

  public static void main(String[] args) {
    SpringApplication.run(PayAppliction.class,args);
  }

  @Bean
  public WXPay wxPay(){
    try {
      return new WXPay(new MyConfig());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
