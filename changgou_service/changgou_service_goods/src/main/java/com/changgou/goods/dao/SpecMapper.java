package com.changgou.goods.dao;

import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecMapper extends Mapper<Spec> {

  @Select("SELECT * FROM tb_spec WHERE template_id in (SELECT template_id FROM tb_category WHERE `name` = #{CategoryName})")
  public List<Spec> findSpecListByCategoryName(@Param("CategoryName") String CategoryName);

}
