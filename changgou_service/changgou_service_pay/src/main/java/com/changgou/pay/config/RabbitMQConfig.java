package com.changgou.pay.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName RabbitMQConfig
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月01日 16:44
 * @Version 1.0.0
*/
public class RabbitMQConfig {

  public static final String ORDER_PAY = "order_pay";

  //声明队列
  @Bean(ORDER_PAY)
  public Queue ORDER_PAY(){
    return new Queue(ORDER_PAY);
  }

}
