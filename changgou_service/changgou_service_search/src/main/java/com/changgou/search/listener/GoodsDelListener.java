package com.changgou.search.listener;

import com.changgou.search.config.RabbitMQConfig;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName GoodsDelListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月22日 0:12
 * @Version 1.0.0
*/
@Component
public class GoodsDelListener {

  @Autowired
  private ESManagerService esManagerService;

  @RabbitListener(queues = RabbitMQConfig.SEARCH_DEL_QUEUE)
  public void receiveMessage(String spuId){

    System.out.println("删除索引库监听接收到的spuId" + spuId);

    //调用业务层完成索引库数据删除
    esManagerService.delDataBySpuId(spuId);
  }
}
