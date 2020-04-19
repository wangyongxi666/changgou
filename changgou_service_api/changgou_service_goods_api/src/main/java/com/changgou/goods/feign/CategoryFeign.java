package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName CategoryFeign
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月23日 12:24
 * @Version 1.0.0
*/
@FeignClient(name = "goods")
public interface CategoryFeign {

  /***
   * 根据ID查询数据
   * @param id
   * @return
   */
  @GetMapping("/category/{id}")
  public Result<Category> findById(@PathVariable Integer id);

}
