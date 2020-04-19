package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.util
        .DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SeckillGoodsPushTask
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年03月02日 20:53
 * @Version 1.0.0
*/
@Component
public class SeckillGoodsPushTask {

  @Autowired
  private SeckillGoodsMapper seckillGoodsMapper;

  @Autowired
  private RedisTemplate redisTemplate;

  //秒杀商品在redis中的前缀
  private static final String SECKILL_GOOD_KEY = "seckill_good";

  //库存在redis中的前缀
  private static final String SECKILL_GOOD_STOCK_COUNT_KEY = "seckill_good_stock_count_key";

  @Scheduled(cron = "0/30 * * * * ?")
  public void loadSecKillGoodsRedis(){
    System.out.println("开始导入数据");

    //1.查询所有符合条件的秒杀商品
    //    1) 获取时间段集合并循环遍历出每一个时间段
    List<Date> dateMenus = DateUtil.getDateMenus();
    System.out.println("五个时间段为：" + dateMenus);
    for (Date dateMenu : dateMenus) {
      //格式化日期
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      //    2) 获取每一个时间段名称,用于后续redis中key的设置
      String redisName = DateUtil.date2Str(dateMenu);


      Example example = new Example(SeckillGoods.class);
      Example.Criteria criteria = example.createCriteria();
      //    3) 状态必须为审核通过 status=1
      criteria.andEqualTo("status","1");
      //    4) 商品库存个数>0
      criteria.andGreaterThan("stockCount",0);
      //    5) 秒杀商品开始时间>=当前时间段
      criteria.andGreaterThanOrEqualTo("startTime",format.format(dateMenu));
      //    6) 秒杀商品结束<当前时间段+2小时
      criteria.andLessThanOrEqualTo("endTime",format.format(DateUtil.addDateHour(dateMenu,2)));

      System.out.println("startTime > " + format.format(dateMenu));
      System.out.println("endTime < " + format.format(DateUtil.addDateHour(dateMenu,2)));

      //    7) 排除之前已经加载到Redis缓存中的商品数据
      Set keys = redisTemplate.boundHashOps(SECKILL_GOOD_KEY + redisName).keys();
      if(keys != null && keys.size() > 0){
        criteria.andNotIn("id",keys);
      }

      //    8) 执行查询获取对应的结果集
      //符合条件的秒杀商品
      List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

      System.out.println("导入的数据为：" + seckillGoods);

      //2.将秒杀商品存入缓存
      for (SeckillGoods seckillGood : seckillGoods) {
        redisTemplate.opsForHash().put(SECKILL_GOOD_KEY + redisName,seckillGood.getId(),seckillGood);

        //加载商品的库存
        redisTemplate.opsForValue().set(SECKILL_GOOD_STOCK_COUNT_KEY+seckillGood.getId(),seckillGood.getStockCount());
      }

      System.out.println("导入数据结束");
    }













  }
}
