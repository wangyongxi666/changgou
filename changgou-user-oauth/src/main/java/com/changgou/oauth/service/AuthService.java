package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;

/**
 * @ClassName AuthService
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月24日 14:21
 * @Version 1.0.0
*/
public interface AuthService {

  AuthToken login(String username,String password,String clientId,String clientSecret);
}
