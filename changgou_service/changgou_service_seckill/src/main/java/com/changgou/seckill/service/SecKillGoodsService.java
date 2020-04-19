package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillGoods;

import java.util.List;

public interface SecKillGoodsService {

  //获取缓存中已加载的秒杀商品
  List<SeckillGoods> list(String time);
}
