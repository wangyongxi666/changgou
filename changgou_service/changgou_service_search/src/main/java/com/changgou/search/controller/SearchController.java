package com.changgou.search.controller;

import com.changgou.elasticsearch.pojo.Page;
import com.changgou.elasticsearch.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName SearchController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月22日 13:01
 * @Version 1.0.0
*/
@RequestMapping("/search")
@Controller
public class SearchController {

  @Autowired
  private SearchService searchService;

  @GetMapping()
  @ResponseBody
  public Map search(@RequestParam Map<String,String> searchMap){
    //特殊符号处理
    this.handleSearchMap(searchMap);

    Map search = searchService.search(searchMap);
    return search;
  }

  //特殊符号处理
  private void handleSearchMap(Map<String, String> searchMap) {
    Set<Map.Entry<String, String>> entries = searchMap.entrySet();
    for (Map.Entry<String, String> entry : entries) {
      if(entry.getKey().startsWith("spec_")){
        searchMap.put(entry.getKey(),entry.getValue().replace(" ","+"));
      }
    }
  }

  //搜索页面   http://localhost:9009/search/list?keywords=手机&brand=三星&spec_颜色=粉色&
  //入参：Map
  //返回值 Map
  //由于页面是thymeleaf 完成的 属于服务器内页面渲染 跳转页面
  @GetMapping("/list")
  public String list(@RequestParam Map<String, String> searchMap, Model model) throws Exception {

    //特殊符号处理
    handleSearchMap(searchMap);

    //执行查询方法
    Map<String, Object> resultMap = searchService.search(searchMap);

    model.addAttribute("searchMap", searchMap);
    model.addAttribute("result", resultMap);

    //封装分页数据 总记录数  当前页  每页显示多少条
    Page<SkuInfo> page = new Page<SkuInfo>((Long)resultMap.get("total"),
           Integer.parseInt((String) resultMap.get("pageNum")),
            Page.pageSize
    );

    //设置分页数据
    model.addAttribute("page",page);

    //拼装url
    StringBuffer url = new StringBuffer("/search/list");

    if(searchMap != null && searchMap.size() > 0){

      url.append("?");

      for (String key : searchMap.keySet()) {

        if(!"sortRule".equals(key) && !"sortField".equals(key) && !"pageNum".equals(key)){
          url.append(key).append("=").append(searchMap.get(key)).append("&");
        }

      }

      String urlString = url.toString();
      //去除最后一个&
      urlString = urlString.substring(0, urlString.length() - 1);
      model.addAttribute("url",urlString);

    }else{
      model.addAttribute("url",url);
    }

    return "search";
  }

}
