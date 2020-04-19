package com.changgou.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName RabbitMQConfig
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 23:41
 * @Version 1.0.0
*/
@Component
public class RabbitMQConfig {

  //秒杀下单队列
  public static  final String SECKILL_ORDER_QUEUE = "seckill_order";


  /**
   * 声明队列
  **/
  //秒杀下单队列
  @Bean(SECKILL_ORDER_QUEUE)
  public Queue SECKILL_ORDER_QUEUE(){
    return new Queue(SECKILL_ORDER_QUEUE,true);//开启队列持久化
  }


}
