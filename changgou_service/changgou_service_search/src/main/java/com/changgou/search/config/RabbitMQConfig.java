package com.changgou.search.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RabbitMQConfig
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月20日 21:58
 * @Version 1.0.0
*/
@Configuration
public class RabbitMQConfig {

  //定义交换机名称
  public static final String GOODS_UP_EXCHANGE = "goods_up_exchange";
  public static final String GOODS_DOWN_EXCHANGE = "goods_down_exchange";

  //定义队列名称
  public static final String AD_UPDATE_QUEUE = "ad_update_queue";
  public static final String SEARCH_ADD_QUEUE = "search_add_queue";
  public static final String SEARCH_DEL_QUEUE = "search_del_queue";

  //声明队列
  @Bean
  public Queue queue(){
    return new Queue(AD_UPDATE_QUEUE);
  }

  /**
   * @Author YongXi.Wang
   * @Description 上架
   * @Date 2020/2/21 23:56
   * @Param [] 
   * @return org.springframework.amqp.core.Exchange
  **/
  //声明队列
  @Bean(SEARCH_ADD_QUEUE)
  public Queue SEARCH_ADD_QUEUE(){
    return new Queue(SEARCH_ADD_QUEUE);
  }

  //声明交换机
  @Bean(GOODS_UP_EXCHANGE)
  public Exchange GOODS_UP_EXCHANGE(){
    return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
  }

  //队列绑定交换机
  @Bean
  public Binding AD_UPDATE_QUEUE_BINDING(@Qualifier(SEARCH_ADD_QUEUE) Queue queue,@Qualifier(GOODS_UP_EXCHANGE) Exchange exchange){
    return BindingBuilder.bind(queue).to(exchange).with("").noargs();
  }

  /**
   * @Author YongXi.Wang
   * @Description 下架
   * @Date 2020/2/21 23:56
   * @Param  
   * @return 
  **/
  @Bean(SEARCH_DEL_QUEUE)
  public Queue SEARCH_DEL_QUEUE(){
    return new Queue(SEARCH_DEL_QUEUE);
  }

  //声明交换机
  @Bean(GOODS_DOWN_EXCHANGE)
  public Exchange GOODS_DOWN_EXCHANGE(){
    return ExchangeBuilder.fanoutExchange(GOODS_DOWN_EXCHANGE).durable(true).build();
  }

  //队列绑定交换机
  @Bean
  public Binding SEARCH_DEL_QUEUE_BINDING(@Qualifier(SEARCH_DEL_QUEUE) Queue queue,@Qualifier(GOODS_DOWN_EXCHANGE) Exchange exchange){
    return BindingBuilder.bind(queue).to(exchange).with("").noargs();
  }

}
