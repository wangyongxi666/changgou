package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.elasticsearch.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.Field;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName SearchServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月22日 11:58
 * @Version 1.0.0
*/
@Service
public class SearchServiceImpl implements SearchService {

  @Autowired
  private ElasticsearchTemplate elasticsearchTemplate;

  @Override
  public Map search(Map<String, String> map) {

    Map<String,Object> resultMap = new HashMap<>();

    if(map != null && !map.isEmpty()){

      //条件构建对象
      NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
      BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

      //按照关键字查询
      if(StringUtils.isNotEmpty(map.get("keywords"))){
        boolQuery.must(QueryBuilders.matchQuery("name",map.get("keywords")).operator(Operator.AND));
      }

      //按照品牌进行查询
      if(StringUtils.isNotEmpty(map.get("brand"))){
        boolQuery.filter(QueryBuilders.termQuery("brandName",map.get("brand")));
      }

      //按照规格进行查询
      for (String key : map.keySet()) {
        if(key.startsWith("spec_")){
          String value = map.get(key).replace("%2B","+");
          boolQuery.filter(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",value));
        }
      }

      //按照价格区间进行查询
      if(StringUtils.isNotEmpty(map.get("price"))){
        String[] prices = map.get("price").split("-");
        if(prices.length == 2){
          boolQuery.filter(QueryBuilders.rangeQuery("price").lte(prices[1]));
        }

        boolQuery.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));

      }

      builder.withQuery(boolQuery);

      //按照品牌分组查询
      String skuBrand = "skuBrand";
      builder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));

      //按照规格进行分组查询
      String skuSpec = "skuSpec";
      builder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));

      //开启分页查询
      String pageNum = map.get("pageNum");
      String pageSize = map.get("pageSize");

      //从0开始
      if(pageNum == null || pageNum.isEmpty()){
        pageNum = "1";
      }
      if(pageSize == null || pageSize.isEmpty()){
        pageSize = "30";
      }
      //设置分页
      builder.withPageable(PageRequest.of(Integer.parseInt(pageNum) - 1,Integer.parseInt(pageSize)));

      //按照相关字段进行排序操作
      //1、当前域  2、当前的排序操作(升序ASC 降序DESC)
      if(StringUtils.isNotEmpty(map.get("sortField")) && StringUtils.isNotEmpty(map.get("sortRule"))){
        if("ASC".equals(map.get("sortRule"))){
          builder.withSort(SortBuilders.fieldSort(map.get("sortField")).order(SortOrder.ASC));
        }else {
          builder.withSort(SortBuilders.fieldSort(map.get("sortField")).order(SortOrder.DESC));
        }
      }

      //设置高亮域和高亮样式
      HighlightBuilder.Field field = new HighlightBuilder.Field("name")
              .preTags("<span style='color:red'>")
              .postTags("</span>");
      builder.withHighlightFields(field);


      //开启查询
      //参数 条件构建对象、查询操作实体类、查询结果操作对象
      AggregatedPage<SkuInfo> resultInfo = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class, new SearchResultMapper() {
        @Override
        public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

          //查询结果操作
          List<T> list = new ArrayList<>();

          //获取查询命中结果数据
          SearchHits hits = searchResponse.getHits();

          if(hits != null){

            for (SearchHit hit : hits) {

              SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);

              //获取并替换高亮域
              Map<String, HighlightField> highlightFields = hit.getHighlightFields(); //取出所有高亮字段
              if(highlightFields != null && highlightFields.size() > 0){
                //highlightFields.get("name") 取出对应的高亮字段 ， .getFragments() 取出各个片段
                skuInfo.setName(highlightFields.get("name").getFragments()[0].toString());
              }

              list.add((T) skuInfo);
            }

          }

          return new AggregatedPageImpl<>(list,pageable,hits.getTotalHits(),searchResponse.getAggregations());
        }
      });

      //封装最终的返回类
      //总记录数
      resultMap.put("total",resultInfo.getTotalElements());
      //总页数
      resultMap.put("totalPages",resultInfo.getTotalPages());
      //数据集合
      resultMap.put("rows",resultInfo.getContent());

      
      //封装品牌的分组结果
      StringTerms brandTerms = (StringTerms) resultInfo.getAggregation(skuBrand);
      List<String> brandList = brandTerms.getBuckets().stream().map(e -> e.getKeyAsString()).collect(Collectors.toList());
      resultMap.put("brandList",brandList);

      //封装规格的分组结果
      StringTerms specTerms = (StringTerms) resultInfo.getAggregation(skuSpec);
      List<String> specList = specTerms.getBuckets().stream().map(e -> e.getKeyAsString()).collect(Collectors.toList());
      resultMap.put("specList",this.formartSpec(specList));

      //当前页
      resultMap.put("pageNum",pageNum);
    }

    return resultMap;
  }

  public Map<String, Set<String>> formartSpec(List<String> specList){
    Map<String,Set<String>> resultMap = new HashMap<>();
    if (specList!=null && specList.size()>0){
      for (String specJsonString : specList) {  //"{'颜色': '黑色', '尺码': '250度'}"
        //将获取到的json转换为map
        Map<String,String> specMap = JSON.parseObject(specJsonString, Map.class);
        for (String specKey : specMap.keySet()) {
          Set<String> specSet = resultMap.get(specKey);
          if (specSet == null){
            specSet = new HashSet<String>();
          }
          //将规格信息存入set中
          specSet.add(specMap.get(specKey));
          //将set存入map
          resultMap.put(specKey,specSet);
        }
      }
    }
    return resultMap;
  }

}
