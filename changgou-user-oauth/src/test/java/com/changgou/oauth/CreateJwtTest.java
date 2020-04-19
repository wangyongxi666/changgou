package com.changgou.oauth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class CreateJwtTest {

   @Test
   public void createJWT(){

     //创建密钥工厂 指定私钥位置 指定密钥库密码
     ClassPathResource resource = new ClassPathResource("changgou.jks");
     String keyPass = "changgou";
     KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,keyPass.toCharArray());

     //获取私钥 密钥别名 密钥密码
     String alias = "changgou";
     String password = "changgou";
     //私钥
     KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, password.toCharArray());

     //将当前私钥转换成rsa私钥
     RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

     //生成jwt
     Map<String,String> map = new HashMap();
     map.put("name","wyx");
     map.put("age","26");

     //jwt令牌内容 签名
     Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(rsaPrivateKey));

     System.out.println(jwt.getEncoded());
   }
}
