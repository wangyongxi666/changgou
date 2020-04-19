package com.changgou.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName FileApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月18日 12:39
 * @Version 1.0.0
*/
@SpringBootApplication
@EnableEurekaClient
public class FileApplication {

  public static void main(String[] args) {
    SpringApplication.run(FileApplication.class);
  }
}
