package com.changgou.consume.dao;

import com.changgou.seckill.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SecKillOrderMapper extends Mapper<SeckillOrder> {

}
