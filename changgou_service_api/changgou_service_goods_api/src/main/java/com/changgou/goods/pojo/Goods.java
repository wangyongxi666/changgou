package com.changgou.goods.pojo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName Goods
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月19日 16:53
 * @Version 1.0.0
*/
@Data
public class Goods {

  private Spu spu;

  private List<Sku> skuList;
}
