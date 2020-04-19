package com.changgou.seckill.service.impl;

import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SecKillGoodsService;
import com.changgou.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SecKillGoodsServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月03日 12:17
 * @Version 1.0.0
*/
@Service
public class SecKillGoodsServiceImpl implements SecKillGoodsService {

  @Autowired
  private RedisTemplate redisTemplate;

  private static final String SECKILL_GOOD_KEY = "seckill_good";

  //库存在redis中的前缀
  private static final String SECKILL_GOOD_STOCK_COUNT_KEY = "seckill_good_stock_count_key";

  @Override
  public List<SeckillGoods> list(String time) {

    List<SeckillGoods> values = redisTemplate.boundHashOps(SECKILL_GOOD_KEY + DateUtil.formatStr(time)).values();

    //更新库存数据来源
    for (SeckillGoods value : values) {
      String count = (String) redisTemplate.opsForValue().get(SECKILL_GOOD_STOCK_COUNT_KEY + value.getId());
      value.setStockCount(Integer.parseInt(count));
    }

    return values;
  }
}
