package com.changgou.order.task;

import com.alibaba.fastjson.JSON;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.Task;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @ClassName QueryPointTask
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月28日 21:43
 * @Version 1.0.0
*/
@Component
public class QueryPointTask {

  @Autowired
  private TaskMapper taskMapper;
  
  @Autowired
  private RabbitTemplate rabbitTemplate;

  //定时轮询任务表最新数据
  @Scheduled(cron = "0/10 * * * * ?")
  public void queryTask(){
    //获取最新数据 小于系统当前时间的数据
    List<Task> taskList = taskMapper.findTaskLessThanCurrentTime(new Date());
    if(taskList != null && taskList.size() > 0){

      for (Task task : taskList) {
        //将任务发送到消息队列
        rabbitTemplate.convertAndSend
                (RabbitMQConfig.EX_BUYING_ADDPOINTUSER,RabbitMQConfig.CG_BUYING_ADDPOINT_KEY, JSON.toJSONString(task));
        System.out.println("订单服务向队列发送了一条消息");
      }

    }



  }
}
