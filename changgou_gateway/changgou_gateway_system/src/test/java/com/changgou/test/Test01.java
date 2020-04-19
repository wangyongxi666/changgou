package com.changgou.test;


import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @ClassName Test01
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月18日 19:29
 * @Version 1.0.0
*/
public class Test01 {

  @Test
  public void test(){
    String gensalt = BCrypt.gensalt();
    System.out.println(gensalt);

    String hashpw = BCrypt.hashpw("123456", gensalt);
    System.out.println(hashpw);

    boolean checkpw = BCrypt.checkpw("123456", hashpw);
    System.out.println(checkpw);
  }
}
