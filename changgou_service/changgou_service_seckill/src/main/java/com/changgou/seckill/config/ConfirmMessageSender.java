package com.changgou.seckill.config;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName ConfirmMessageSender
 * @Description   增强消息队列，携带唯一标识进行发送消息，
 *                  并监听持久化操作的执行结果，
 *                  如果失败，则从nosql中重新读取 消息并发送到队列，
 *                  如果成功则把消息从nosql中删除
 * @Author YongXi.Wang
 * @Date  2020年03月03日 23:45
 * @Version 1.0.0
*/
@Component
public class ConfirmMessageSender implements RabbitTemplate.ConfirmCallback{

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private RedisTemplate redisTemplate;

  private static final String MESSAGE_CONFIRM_KEY = "message_confirm";

  public ConfirmMessageSender(RabbitTemplate rabbitTemplate){
    this.rabbitTemplate = rabbitTemplate;
    rabbitTemplate.setConfirmCallback(this);
  }

  //接收消息服务器返回的通知
  @Override
  public void confirm(CorrelationData correlationData, boolean result, String s) {

    //成功通知，说明消息服务器已经对消息进行持久化操作,需要删除redis中的消息
    String id = correlationData.getId();
    if(result){

      //删除redis中的消息体
      redisTemplate.delete(id);

      //删除redis中的元数据
      redisTemplate.delete(MESSAGE_CONFIRM_KEY + id);
    }else{
      //失败通知,需要把消息取出，进行重新发送
      Map<String,String> map = redisTemplate.opsForHash().entries(MESSAGE_CONFIRM_KEY + id);
      String exchange = map.get("exchange");
      String routingKey = map.get("routingKey");
      String message = map.get("message");

      //携带本次消息的唯一标识，进行数据发送
      rabbitTemplate.convertAndSend(exchange,routingKey, JSON.toJSONString(message),correlationData);
    }

  }

  //自定义消息发送方式
  public void sendMessage(String exchange,String routingKey,String message){

    //设置消息的唯一标识并存入到redis中
    CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
    redisTemplate.opsForValue().set(correlationData.getId(),message);

    //将本次发送消息的相关元数据保存到redis中
    Map<String,String> map = new HashMap<>();
    map.put("exchange",exchange);
    map.put("routingKey",routingKey);
    map.put("message",message);

    redisTemplate.opsForHash().putAll(MESSAGE_CONFIRM_KEY + correlationData.getId(),map);

    //携带本次消息的唯一标识，进行数据发送
    rabbitTemplate.convertAndSend(exchange,routingKey,message,correlationData);

  }
}
