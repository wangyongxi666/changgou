package com.changgou.page.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;
import com.changgou.goods.feign.SpuFeign;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PageServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月23日 13:27
 * @Version 1.0.0
*/
@Service
  public class PageServiceImpl implements PageService {

  @Autowired
  public SpuFeign spuFeign;

  @Autowired
  private CategoryFeign categoryFeign;

  @Autowired
  private SkuFeign skuFeign;

  @Value("${pagepath}")
  private String pagepath;

  @Autowired
  private TemplateEngine templateEngine;

  @Override
  public void generateHtml(String spuId) {

    //获取context对象，用于存储商品的相关数据
    Context context = new Context();
    Map itemDataMap = this.getItemData(spuId);
    context.setVariables(itemDataMap);

    //获取商品详情页面出储存位置
    File dir = new File(pagepath);

    //判断当前文件夹是否存在，不存在则新建
    if(!dir.exists()){
      dir.mkdirs();
    }

    //定义输出流
    File file = new File(dir + File.separator + spuId + ".html");
    Writer writer = null;

    try {

      //生成静态化页面
      writer = new PrintWriter(file);

      //模板名称 context 输出流
      templateEngine.process("item",context,writer);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      //关闭流
      if(writer != null){
        try {
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private Map getItemData(String spuId) {

    Map<String,Object> map = new HashMap<>();

    //spu数据
    Spu spu = spuFeign.findSpuById(spuId).getData();

    //图片信息
    if(spu != null && StringUtils.isNotEmpty(spu.getImages())){
      map.put("imageList",spu.getImages().split(","));
    }

    //商品分类信息
    Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
    Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
    Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();

    //sku相关信息
    List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);

    //获取商品规格信息
    map.put("specificationList", JSON.parseObject(spu.getSpecItems(),Map.class));


    //封装信息
    map.put("spu",spu);
    map.put("category1",category1);
    map.put("category2",category2);
    map.put("category3",category3);
    map.put("skuList",skuList);

    return map;
  }

}
