package com.itheima.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.itheima.canal.config.RabbitMQConfig;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ZJ
 */
@CanalEventListener
public class BusinessListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    private RabbitTemplate rabbitTemplate;

//    @ListenPoint(schema = "changgou_business", table = {"tb_ad"})
//    public void adUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
//        System.err.println("广告数据发生变化");
//
//        //修改前数据
//        for(CanalEntry.Column column: rowData.getBeforeColumnsList()) {
//            if(column.getName().equals("position")){
//                System.out.println("发送消息到mq  ad_update_queue:"+column.getValue());
//                rabbitTemplate.convertAndSend("","ad_update_queue",column.getValue());  //发送消息到mq
//                break;
//            }
//        }
//
//        //修改后数据
//        for(CanalEntry.Column column: rowData.getAfterColumnsList()) {
//            if(column.getName().equals("position")){
//                System.out.println("发送消息到mq  ad_update_queue:"+column.getValue());
//                rabbitTemplate.convertAndSend("","ad_update_queue",column.getValue());  //发送消息到mq
//                break;
//            }
//        }
//    }


    /**
     * @Author YongXi.Wang
     * @Description //TODO
     * @Date 2020/2/20 21:23
     * @Param [eventType 当前操作数据库的类型, rowData 当前操作的数据库数据]
     * @return void
    **/
    @ListenPoint(schema = "changgou_business",table = "tb_ad") //监听的数据和表
    public void adUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        System.out.println("广告表数据发生改变");

//        //获取改变前的数据
//        rowData.getBeforeColumnsList().forEach(e -> System.out.println("改变前：" + e.getName() + e.getValue()));
//
//        //获取改变后的数据
//        rowData.getAfterColumnsList().forEach(e -> System.out.println("改变后：" + e.getName() + e.getValue()));

        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {

            if("position".equals(column.getName())){
                System.out.println("发送消息到MQ：" + column.getValue());
            }

            //发送消息
            rabbitTemplate.convertAndSend("", RabbitMQConfig.AD_UPDATE_QUEUE,column.getValue());
        }
    }

}
