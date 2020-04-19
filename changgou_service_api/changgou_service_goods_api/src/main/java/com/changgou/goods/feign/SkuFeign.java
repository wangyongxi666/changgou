package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SkuFeign
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月21日 19:30
 * @Version 1.0.0
*/
@FeignClient(name = "goods")
//@RequestMapping("/sku")
public interface SkuFeign {

  @GetMapping("/sku/spu/{spuId}")
  public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

  @GetMapping("/sku/{id}")
  public Result<Sku> findById(@PathVariable String id);

  @PostMapping("/sku/decr/count")
  public Result decrCount(@RequestParam String username) throws Exception;

  @RequestMapping("/sku/resumeStockNum")
  public Result resumeStockNum(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num);
}
