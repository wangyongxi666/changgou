package com.changgou.seckill.service.impl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.config.ConfirmMessageSender;
import com.changgou.seckill.config.RabbitMQConfig;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SecKillOrderService;
import com.changgou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @ClassName SecKillOrderServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 19:40
 * @Version 1.0.0
*/
@Service
public class SecKillOrderServiceImpl implements SecKillOrderService {

  private static final String SECKILL_GOOD_KEY = "seckill_good";
  private static final String SECKILL_GOOD_STOCK_COUNT_KEY = "seckill_good_stock_count_key";

  @Autowired
  private SeckillOrderMapper seckillOrderMapper;

  @Autowired
  private ConfirmMessageSender confirmMessageSender;

  @Autowired
  private RedisTemplate redisTemplate;

  @Autowired
  private IdWorker idWorker;

  @Override
  public boolean add(Long id, String time, String username) {

    //防止刷单
    boolean auth = this.preventRepeatCommit(id,username);
    if(!auth){
      return false;
    }

    //每个商品每个人限定只能买一次
    SeckillOrder order = seckillOrderMapper.getOrderInfoByUsernameAndGoodsId(username, id);
    if(order != null){
      return false;
    }

    //查询redis中是否有秒杀的商品
    SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SECKILL_GOOD_KEY + time).get(id);
    if(seckillGoods == null ){
      return false;
    }

    //查询redis商品库存，看是否充足 (因为对redis进行了string序列化，所以取出来的值都为String)
    String countStr = (String) redisTemplate.opsForValue().get(SECKILL_GOOD_STOCK_COUNT_KEY + id);
    if(countStr == null ){
      return false;
    }

    //转换成int
    int count = Integer.parseInt(countStr);
    if(count <= 0){
      return false;
    }

    //执行redis库存扣减（由于是秒杀商品，只允许每人每次抢购一件）
    Long decremented = redisTemplate.opsForValue().decrement(SECKILL_GOOD_STOCK_COUNT_KEY + id);

    //操作后的库存小于等于0，则把该商品从redis 中删除
    if(decremented <= 0){
      //删除商品
      redisTemplate.boundHashOps(SECKILL_GOOD_KEY + time).delete(id);
      //删除商品库存
      redisTemplate.delete(SECKILL_GOOD_STOCK_COUNT_KEY + id);
    }

    //发送消息，保证消息生产者对消息的不丢失实现
    //消息体：秒杀订单
    SeckillOrder seckillOrder = new SeckillOrder();
    seckillOrder.setId(Long.parseLong(idWorker.nextId() + ""));
    seckillOrder.setSeckillId(id);
    seckillOrder.setMoney(seckillGoods.getCostPrice());
    seckillOrder.setUserId(username);
    seckillOrder.setSellerId(seckillGoods.getSellerId());
    seckillOrder.setCreateTime(new Date());
    seckillOrder.setPayTime(new Date());
    seckillOrder.setStatus("0");

    //发送消息 开启rb的持久化
    confirmMessageSender.sendMessage("", RabbitMQConfig.SECKILL_ORDER_QUEUE, JSON.toJSONString(seckillOrder));

    return true;
  }

  //防止刷单操作
  private boolean preventRepeatCommit(Long id, String username) {

    String redisName = "seckill_user_" + username + "_id_" + id;

    long increment = redisTemplate.opsForValue().increment(redisName, 1);

    if(increment == 1){
      //代表用户第一次访问
      //对当前key设置一个五分钟有效期
      redisTemplate.expire(redisName,5, TimeUnit.MINUTES);

      return true;
    }else {
      return false;
    }

  }
}
