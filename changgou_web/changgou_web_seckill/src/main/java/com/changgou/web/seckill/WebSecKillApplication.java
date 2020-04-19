package com.changgou.web.seckill;

import com.changgou.interceptor.FeignInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @ClassName WebSecKillApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date 2020年03月03日 1:36
 * @Version 1.0.0
 */

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.seckill.feign"})
public class WebSecKillApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebSecKillApplication.class, args);
  }

  @Bean
  public FeignInterceptor feignInterceptor(){
    return new FeignInterceptor();
  }

  /**
   * 设置 redisTemplate 的序列化设置
   * @param redisConnectionFactory
   * @return
   */
  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    // 1.创建 redisTemplate 模版
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    // 2.关联 redisConnectionFactory
    template.setConnectionFactory(redisConnectionFactory);
    // 3.创建 序列化类
    GenericToStringSerializer genericToStringSerializer = new GenericToStringSerializer(Object.class);
    // 6.序列化类，对象映射设置
    // 7.设置 value 的转化格式和 key 的转化格式
    template.setValueSerializer(genericToStringSerializer);
    template.setKeySerializer(new StringRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }

}
