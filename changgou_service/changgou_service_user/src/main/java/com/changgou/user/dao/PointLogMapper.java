package com.changgou.user.dao;

import com.changgou.user.pojo.PointLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface PointLogMapper extends Mapper<PointLog> {

  @Select("SELECT * FROM tb_point_log WHERE order_id =#{orderId}")
  PointLog findPointLogByOrderId(@Param("orderId") String orderId);
}
