package com.changgou.goods.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName CategoryBrand
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月19日 18:19
 * @Version 1.0.0
*/
@Table(name = "tb_category_brand")
@Data
public class CategoryBrand {

  @Id
  private Integer categoryId;

  @Id
  private Integer brandId;
}
