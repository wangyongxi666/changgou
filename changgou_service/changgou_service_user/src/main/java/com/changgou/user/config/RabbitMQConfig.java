package com.changgou.user.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @ClassName RabbitMQConfig
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月28日 20:37
 * @Version 1.0.0
*/
@Configuration
public class RabbitMQConfig {
  /**
   * 交换机
  **/
  //添加积分任务交换机
  public static final String EX_BUYING_ADDPOINTUSER = "ex_buying_addpointuser";

  /**
   * 消息队列
   **/
  //添加积分消息队列
  public static final String CG_BUYING_ADDPOINT = "cg_buying_addpoint";
  //完成添加积分消息队列
  public static final String CG_BUYING_FINISHADDPOINT = "cg_buying_finishaddpoint";

  /**
   * 路由key
   **/
  //添加积分路由key
  public static final String CG_BUYING_ADDPOINT_KEY = "addpoint";
  //完成添加积分路由key
  public static final String CG_BUYING_FINISHADDPOINT_KEY = "finishaddpoint";


  /**
   * 声明交换机
  **/
  //声明交换机
  @Bean(EX_BUYING_ADDPOINTUSER)
  public Exchange EX_BUYING_ADDPOINTUSER(){
    return ExchangeBuilder.directExchange(EX_BUYING_ADDPOINTUSER).durable(true).build();
  }


  /**
   * 声明队列
   **/
  //声明添加积分队列
  @Bean(CG_BUYING_ADDPOINT)
  public Queue CG_BUYING_ADDPOINT(){
    return new Queue(CG_BUYING_ADDPOINT);
  }

  //声明完成添加积分队列
  @Bean(CG_BUYING_FINISHADDPOINT)
  public Queue CG_BUYING_FINISHADDPOINT(){
    return new Queue(CG_BUYING_FINISHADDPOINT);
  }


  /**
   * 队列绑定交换机
  **/
  //添加积分
  @Bean
  public Binding BINDING_CG_BUYING_ADDPOINT(@Qualifier(CG_BUYING_ADDPOINT) Queue queue,
                                            @Qualifier(EX_BUYING_ADDPOINTUSER) Exchange exchange){
    return BindingBuilder.bind(queue).to(exchange).with(CG_BUYING_ADDPOINT_KEY).noargs();
  }

  //完成积分
  @Bean
  public Binding BINDING_CG_BUYING_FINISHADDPOINT(@Qualifier(CG_BUYING_FINISHADDPOINT) Queue queue,
                                                  @Qualifier(EX_BUYING_ADDPOINTUSER) Exchange exchange){
    return BindingBuilder.bind(queue).to(exchange).with(CG_BUYING_FINISHADDPOINT_KEY).noargs();

  }





}