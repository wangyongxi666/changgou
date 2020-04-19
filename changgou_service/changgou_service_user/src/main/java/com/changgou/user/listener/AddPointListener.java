package com.changgou.user.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.pojo.Task;
import com.changgou.user.config.RabbitMQConfig;
import com.changgou.user.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @ClassName AddPointListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月28日 22:01
 * @Version 1.0.0
*/
@Component
public class AddPointListener {

  @Autowired
  private RedisTemplate redisTemplate;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private UserService userService;

  @RabbitListener(queues = RabbitMQConfig.CG_BUYING_ADDPOINT)
  public void receiveAddPointMessage(String message){
    System.out.println("用户服务接收到了任务消息");

    //转换消息
    Task task = JSON.parseObject(message, Task.class);
    if(task == null || StringUtils.isEmpty(task.getRequestBody())){
      return;
    }

    //判断redis中是否存在任务
    Object value = redisTemplate.boundValueOps(task.getId()).get();
    if(value != null){
      return;
    }

    //更新积分
    int count = userService.updateUserPoint(task);
    if(count <= 0){
      return ;
    }

    //向订单服务返回通知消息
    rabbitTemplate.convertAndSend
            (RabbitMQConfig.EX_BUYING_ADDPOINTUSER,RabbitMQConfig.CG_BUYING_FINISHADDPOINT_KEY,JSON.toJSONString(task));
  }

}
