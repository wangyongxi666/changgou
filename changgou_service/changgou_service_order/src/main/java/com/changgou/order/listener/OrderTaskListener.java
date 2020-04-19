package com.changgou.order.listener;

import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderTaskListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月02日 15:08
 * @Version 1.0.0
*/
@Component
public class OrderTaskListener {

  private OrderService orderService;

  @RabbitListener(queues = RabbitMQConfig.ORDER_TACK)
  public void receiveOrderTaskMessage(){
    System.out.println("收到自动确认收货的消息");

    orderService.autoTack();

  }
}
