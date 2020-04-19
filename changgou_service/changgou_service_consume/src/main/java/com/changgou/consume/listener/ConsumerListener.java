package com.changgou.consume.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.consume.config.RabbitMQConfig;
import com.changgou.consume.service.SecKillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName consumerListener
 * @Description   消息的手动应答模式
 * @Author YongXi.Wang
 * @Date  2020年03月04日 14:32
 * @Version 1.0.0
*/
@Component
public class ConsumerListener {

  @Autowired
  private SecKillOrderService secKillOrderService;

  @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
  public void receiveSecKillOrderMessage(Message message, Channel channel){

    //设置与抓取总数
    try {
      channel.basicQos(300);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //转换消息格式
    SeckillOrder seckillOrder =  JSON.parseObject(message.getBody(), SeckillOrder.class);

    //完成数据库的同步操作
    int result = secKillOrderService.createOrder(seckillOrder);

    if(result > 0){
      //同步成功 向消息服务器返回成功通知
      try {
        //消息唯一标识  是否开启批处理
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }else{
      //同步mysql失败
      //向消息服务器返回失败通知
      try {
        //消息唯一标识
        // true所有消费者都会拒绝此消息，false只有当前消费者会拒绝此消息
        // true当前消息会进入到死信队列 ， false 当前消息会重新进入到原有队列头部
        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}
