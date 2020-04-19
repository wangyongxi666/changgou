package com.changgou.task.task;

import com.changgou.task.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName OrderTask
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月02日 15:00
 * @Version 1.0.0
*/
@Component
public class OrderTask {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Scheduled(cron = "0 0 0 * * ?")
  public void autoTake(){

    System.out.println(new Date());

    //往自动确认收货的消息队列发消息
    rabbitTemplate.convertAndSend("", RabbitMQConfig.ORDER_TACK,"应该自动收货了");
  }
}
