package com.changgou.search.listener;

import com.changgou.search.config.RabbitMQConfig;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName GoodsUpLIstener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月21日 21:18
 * @Version 1.0.0
*/
@Component
public class GoodsUpLIstener {

  @Autowired
  private ESManagerService esManagerService;

  @RabbitListener(queues = RabbitMQConfig.SEARCH_ADD_QUEUE)
  public void receiveMassage(String spuId){
    System.out.println("接收到的消息为：" + spuId);

    //查询skulist，并导入到索引库
    esManagerService.importDataBySpuId(spuId);
  }
}
