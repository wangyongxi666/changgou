package com.changgou.page.listener;

import com.changgou.page.config.RabbitMQConfig;
import com.changgou.page.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName PageListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月23日 15:23
 * @Version 1.0.0
*/
@Component
public class PageListener {

  @Autowired
  private PageService pageService;

  @RabbitListener(queues = RabbitMQConfig.PAGE_CREATE_QUEUE)
  public void receiveMessage(String spuId){

    System.out.println("获取静态页面生成参数 spuId ：" + spuId);

    pageService.generateHtml(spuId);
  }

}
