package com.changgou.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @ClassName ResourceServerConfig
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 10:54
 * @Version 1.0.0
*/
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//激活方法上的PreAuthorize注解
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  //公钥
  private static final String PUBLIC_KEY = "public.key";

  /***
   * 定义JwtTokenStore
   * @param jwtAccessTokenConverter
   * @return
   */
  @Bean
  public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
    System.out.println("11111111111111");
    return new JwtTokenStore(jwtAccessTokenConverter);
  }

  /***
   * 定义JJwtAccessTokenConverter
   * @return
   */
  @Bean
  public JwtAccessTokenConverter jwtAccessTokenConverter() {
    System.out.println("1111111111111");
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setVerifierKey(getPubKey());
    return converter;
  }
  /**
   * 获取非对称加密公钥 Key
   * @return 公钥 Key
   */
  private String getPubKey() {
    System.out.println("1111111111111111111");
    Resource resource = new ClassPathResource(PUBLIC_KEY);
    try {
      InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
      BufferedReader br = new BufferedReader(inputStreamReader);
      return br.lines().collect(Collectors.joining("\n"));
    } catch (IOException ioe) {
      return null;
    }
  }

  /***
   * Http安全配置，对每个到达系统的http请求链接进行校验
   * @param http
   * @throws Exception
   */
  @Override
  public void configure(HttpSecurity http) throws Exception {
    //所有请求必须认证通过
    http.authorizeRequests()
            //下边的路径放行
            .antMatchers(
                    "/user/add","/user/load/{username}","/user/{username}"). //配置地址放行
            permitAll()
            .anyRequest().
            authenticated();    //其他地址需要认证授权
  }
}