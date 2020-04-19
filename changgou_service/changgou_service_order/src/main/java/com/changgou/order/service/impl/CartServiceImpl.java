package com.changgou.order.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CartServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月25日 13:18
 * @Version 1.0.0
*/
@Service
public class CartServiceImpl implements CartService {

  @Autowired
  private RedisTemplate redisTemplate;

  private String CART_Prefix = "cart_";

  @Autowired
  private SkuFeign skuFeign;

  @Autowired
  private SpuFeign spuFeign;

  @Override
  public void addCart(String skuId, Integer num, String username) {

    //查询redis中的sku信息
    OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(CART_Prefix + username).get(skuId);

    /**
     *  num = 3  onum = 5 pri= 10
     * orderItem.setPrice(orderItem.getPrice() + (orderItem.getPrice() * (orderItem.getNum() - num) ));
     * orderItem.setPrice(orderItem.getPrice() + (orderItem.getPrice() - (orderItem.getPrice() - num) ));
    **/
    //如果已经存在，则更新商品数量，和价格
    if(orderItem != null){

      orderItem.setNum(orderItem.getNum() + num);
      //判断商品数量如果小于等于0，则把该商品直接从redis中删除
      if(orderItem.getNum() <= 0){
        redisTemplate.boundHashOps(CART_Prefix + username).delete(skuId);
        return;
      }

      //商品数量大于0，则正常执行流程
      orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
      orderItem.setPayMoney(orderItem.getMoney());

    }else {
      //不存在，则把商品添加到redis中
      Sku sku = skuFeign.findById(skuId).getData();
      Spu spu = spuFeign.findSpuById(sku.getSpuId()).getData();

      //封装orderItem
      orderItem = this.sku2OrderItem(sku,spu,num);
    }

    //将orderItem添加到redis中
    redisTemplate.boundHashOps(CART_Prefix + username).put(skuId,orderItem);

  }

  @Override
  public Map list(String username) {
    Map map = new HashMap();

    List<OrderItem> orderItemList = redisTemplate.boundHashOps(CART_Prefix + username).values();
    map.put("orderItemList",orderItemList);

    //商品总数量
    Integer totalNum = 0;
    //商品总价格
    Integer totalMoney = 0;

    for (OrderItem orderItem : orderItemList) {
      totalNum = totalNum + orderItem.getNum();
      totalMoney = totalMoney + orderItem.getMoney();
    }

    map.put("totalNum",totalNum);
    map.put("totalMoney",totalMoney);

    return map;
  }

  //sku转换为orderItem
  private OrderItem sku2OrderItem(Sku sku, Spu spu, Integer num) {
    OrderItem orderItem = new OrderItem();
    orderItem.setSpuId(sku.getSpuId());
    orderItem.setSkuId(sku.getId());
    orderItem.setName(sku.getName());
    orderItem.setPrice(sku.getPrice());
    orderItem.setNum(num);
    orderItem.setMoney(num*orderItem.getPrice()); //单价*数量
    orderItem.setPayMoney(num*orderItem.getPrice()); //实付金额
    orderItem.setImage(sku.getImage());
    orderItem.setWeight(sku.getWeight()*num); //重量=单个重量*数量

    //分类ID设置
    orderItem.setCategoryId1(spu.getCategory1Id());
    orderItem.setCategoryId2(spu.getCategory2Id());
    orderItem.setCategoryId3(spu.getCategory3Id());
    return orderItem;
  }

}
