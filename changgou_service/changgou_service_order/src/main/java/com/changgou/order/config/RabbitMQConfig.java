package com.changgou.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


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
  //死信交换机
  public static final String EXCHANGE_ORDERTIMEOUT= "exchange.ordertimeout";

  /**
   * 消息队列
   **/
  //添加积分消息队列
  public static final String CG_BUYING_ADDPOINT = "cg_buying_addpoint";
  //完成添加积分消息队列
  public static final String CG_BUYING_FINISHADDPOINT = "cg_buying_finishaddpoint";
  public static final String ORDER_PAY = "order_pay";
  //自动收货
  public static final String ORDER_TACK = "order_tack";
  //死信队列 用于操作订单关闭
  public static final String QUEUE_ORDERTIMEOUT = "queue.ordertimeout";
  //用于订单创建时发出消息 失效时进入死信队列
  public static final String QUEUE_ORDERCREATE = "queue.ordercreate";

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
  //声明死信交换机
  @Bean(EXCHANGE_ORDERTIMEOUT)
  public FanoutExchange EXCHANGE_ORDERTIMEOUT(){
    return new FanoutExchange(EXCHANGE_ORDERTIMEOUT,true,false);
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

  //声明支付成功回调队列
  @Bean(ORDER_PAY)
  public Queue ORDER_PAY(){
    return new Queue(ORDER_PAY);
  }

  //自动确认收货
  @Bean(ORDER_TACK)
  public Queue ORDER_TACK(){return new Queue(ORDER_TACK);}

  //订单创建时发出消息 失效后进入死信队列
  @Bean(QUEUE_ORDERCREATE)
  public Queue QUEUE_ORDERCREATE(){
    Map<String, Object> args = new HashMap<>(2);
    // x-message-ttl   超时时间，超过时间，消息会被抛弃
    args.put("x-message-ttl", 10000);
    // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
    args.put("x-dead-letter-exchange", EXCHANGE_ORDERTIMEOUT);
    return QueueBuilder.durable(QUEUE_ORDERCREATE).withArguments(args).build();
  }

  //收到消息后，进行关闭订单操作
  @Bean(QUEUE_ORDERTIMEOUT)
  public Queue QUEUE_ORDERTIMEOUT(){return new Queue(QUEUE_ORDERTIMEOUT);}





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

  //订单超时没被处理 则被传送到死信队列中
  @Bean
  public Binding BINDING_QUEUE_ORDERCREATE(@Qualifier(QUEUE_ORDERTIMEOUT) Queue queue,
                                                  @Qualifier(EXCHANGE_ORDERTIMEOUT) Exchange exchange){
    return BindingBuilder.bind(queue).to(exchange).with("").noargs();

  }




}