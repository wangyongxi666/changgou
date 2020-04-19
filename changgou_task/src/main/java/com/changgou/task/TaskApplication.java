package com.changgou.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName TaskApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月02日 14:55
 * @Version 1.0.0
*/
@SpringBootApplication
@EnableScheduling
public class TaskApplication {

  public static void main(String[] args) {
    SpringApplication.run( TaskApplication.class,args );
  }
}