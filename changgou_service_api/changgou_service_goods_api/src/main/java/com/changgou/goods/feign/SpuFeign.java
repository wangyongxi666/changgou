package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName SpuFeign
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月23日 12:28
 * @Version 1.0.0
*/
@FeignClient(name = "goods")
public interface SpuFeign {

  @GetMapping("/spu/findSpuById/{id}")
  public Result<Spu> findSpuById(@PathVariable("id") String id);

}
