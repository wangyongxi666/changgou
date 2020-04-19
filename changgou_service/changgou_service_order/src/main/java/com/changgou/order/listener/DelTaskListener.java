package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.pojo.Task;
import com.changgou.order.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName DelTaskListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月28日 23:45
 * @Version 1.0.0
*/
@Component
public class DelTaskListener {

  @Autowired
  private TaskService taskService;

  @RabbitListener(queues = RabbitMQConfig.CG_BUYING_FINISHADDPOINT)
  public void receiveDelTaskMessage(String message){
    System.out.println("订单服务接收到了删除任务的操作消息");

    Task task = JSON.parseObject(message, Task.class);

    //删除原有任务记录  增加历史任务记录
    taskService.delTask(task);

  }

}
