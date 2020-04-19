package com.changgou.task.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RabbitMQConfig
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月02日 14:57
 * @Version 1.0.0
*/
@Configuration
public class RabbitMQConfig {

  public static final String ORDER_TACK="order_tack";

  @Bean
  public Queue ORDER_TACK(){
    return new Queue(ORDER_TACK);
  }

}
