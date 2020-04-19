package com.changgou.consume.service;
import com.changgou.seckill.pojo.SeckillOrder; /**
 * @ClassName SecKillOrderService
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月04日 14:59
 * @Version 1.0.0
*/
public interface SecKillOrderService {

  //新增秒杀订单
  public int createOrder(SeckillOrder seckillOrder) ;
}
