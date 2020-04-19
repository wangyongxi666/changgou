package com.changgou.search.service;
/**
 * @ClassName ESManagerService
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月21日 19:43
 * @Version 1.0.0
*/
public interface ESManagerService {

  //创建索引库结构
  void createMappingAndIndex();

  //导入全部数据进去es
  void importAll();

  //根据spuid查询skuList，再导入索引库
  void importDataBySpuId(String spuId);

  //根据spuid删除索引库
  void delDataBySpuId(String spuId);
}
