package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @ClassName OrderPayListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月01日 18:15
 * @Version 1.0.0
*/
@Component
@Slf4j
public class OrderPayListener {

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  private OrderService orderService;

  @RabbitListener(queues = RabbitMQConfig.ORDER_PAY)
  public void receivePayMessage(String message){
    log.info("接收到了订单完成支付的成功回调{}",message);

    Map map = JSON.parseObject(message, Map.class);

    //调用业务层 完成数据库修改
    orderService.updatePayStatus((String) map.get("orderId"),(String) map.get("transactionId"));

  }
}
