package com.changgou.seckill.dao;

import com.changgou.seckill.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;


public interface SeckillOrderMapper extends Mapper<SeckillOrder> {

  @Select("SELECT * FROM tb_seckill_order WHERE user_id = #{username} AND seckill_id = #{id}")
  SeckillOrder getOrderInfoByUsernameAndGoodsId(@Param("username")String username,@Param("id") Long id);
  
}
