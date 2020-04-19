package com.changgou.search.service;

import java.util.Map;

/**
 * @ClassName SearchService
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月22日 11:55
 * @Version 1.0.0
*/
public interface SearchService {

  //按照查询条件进行查询
  Map search(Map<String, String> map);


}
