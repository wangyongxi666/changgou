package com.changgou.oauth;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Spu;
import com.changgou.user.feign.UserFeign;
import com.changgou.user.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName UserFeginTest
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 20:05
 * @Version 1.0.0
*/
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserFeginTest {

  @Autowired
  private UserFeign userFeign;

  @Autowired
  private SpuFeign spuFeign;

  @Test
  public void test01(){
//    User itheima = userFeign.findUserInfo("heima");
//    System.out.println(itheima);

    Spu data = spuFeign.findSpuById("10000000616300").getData();
    System.out.println(data);

  }
}
