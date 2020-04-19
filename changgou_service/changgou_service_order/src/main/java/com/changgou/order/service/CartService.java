package com.changgou.order.service;

import java.util.Map;

/**
 * @ClassName CartService
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月25日 13:16
 * @Version 1.0.0
*/
public interface CartService {

  //添加购物车
  void addCart(String skuId,Integer num,String username);

  //查询购物车列表数据
  Map list(String username);
}
