package com.changgou.order.listener;

import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName OrderTimeOutListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月02日 0:51
 * @Version 1.0.0
*/
@Component
public class OrderTimeOutListener {

  @Autowired
  private OrderService orderService;

  @RabbitListener(queues = RabbitMQConfig.QUEUE_ORDERTIMEOUT)
  public void receiveCloseOrderMessage(String message){
    System.out.println("接收到关闭订单的信息：" + message);

    try {
      orderService.closeOrder(message);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
