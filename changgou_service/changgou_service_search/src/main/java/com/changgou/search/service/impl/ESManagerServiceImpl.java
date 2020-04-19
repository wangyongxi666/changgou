package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.elasticsearch.pojo.SkuInfo;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ESManagerServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月21日 19:45
 * @Version 1.0.0
*/
@Service
public class ESManagerServiceImpl implements ESManagerService {

  @Autowired
  private ElasticsearchTemplate elasticsearchTemplate;

  @Autowired
  private SkuFeign skuFeign;

  @Autowired
  private ESManagerMapper esManagerMapper;

  @Override
  public void createMappingAndIndex() {

    //创建索引
    elasticsearchTemplate.createIndex(SkuInfo.class);

    //创建映射
    elasticsearchTemplate.putMapping(SkuInfo.class);

  }

  @Override
  public void importAll() {

    //查询sku集合
    List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
    if(skuList == null || skuList.isEmpty()){
      throw new RuntimeException("当前没有查询到数据，无法导入索引库");
    }

    //skulist转换为json
    String jsonSkuList = JSON.toJSONString(skuList);
    //将json转换为skuinfo
    List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

    for (SkuInfo skuInfo : skuInfoList) {
      //将规格信息转换成map
      Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
      skuInfo.setSpecMap(map);
    }

    //导入索引库
    esManagerMapper.saveAll(skuInfoList);
  }

  //根据spuid查询skuList，添加到索引库
  @Override
  public void importDataBySpuId(String spuId) {

    //查询sku数据
    List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
    if(skuList == null || skuList.isEmpty()){
      throw new RuntimeException("当前没有查询到数据，无法导入索引库");
    }

    //skulist转换为json
    String jsonSkuList = JSON.toJSONString(skuList);
    //将json转换为skuinfo
    List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

    for (SkuInfo skuInfo : skuInfoList) {
      //将规格转换成map
      Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
      skuInfo.setSpecMap(map);
    }

    //导入索引库
    esManagerMapper.saveAll(skuInfoList);
  }

  @Override
  public void delDataBySpuId(String spuId) {

    //查询sku数据
    List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
    if(skuList == null || skuList.isEmpty()){
      throw new RuntimeException("当前没有查询到数据，无法删除索引库");
    }

    for (Sku sku : skuList) {

      esManagerMapper.deleteById(Long.parseLong(sku.getId()));

    }

  }
}
