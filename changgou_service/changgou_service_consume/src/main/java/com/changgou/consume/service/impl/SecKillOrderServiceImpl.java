package com.changgou.consume.service.impl;

import com.changgou.consume.dao.SecKillGoodsMapper;
import com.changgou.consume.dao.SecKillOrderMapper;
import com.changgou.consume.service.SecKillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName SecKillOrderServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月04日 15:01
 * @Version 1.0.0
*/
@Service
public class SecKillOrderServiceImpl implements SecKillOrderService {

  @Autowired
  private SecKillOrderMapper secKillOrderMapper;

  @Autowired
  private SecKillGoodsMapper secKillGoodsMapper;

  @Override
  @Transactional
  public int createOrder(SeckillOrder seckillOrder) {
    //扣减库存
    int resultCount = secKillGoodsMapper.updateStockCount(seckillOrder.getSeckillId());
    if(resultCount <= 0){
      return 0;
    }

    ////保存入数据库
    int orderResult = secKillOrderMapper.insertSelective(seckillOrder);
    if(orderResult <= 0){
      return 0;
    }

    return 1;
  }
}
