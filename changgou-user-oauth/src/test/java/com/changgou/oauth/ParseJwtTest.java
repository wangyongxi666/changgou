package com.changgou.oauth;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class ParseJwtTest {

  @Test
  public void parseJwt(){

    //基于公钥去解析jwt
    String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4MjY0OTE4OCwiYXV0aG9yaXRpZXMiOlsiYWNjb3VudGFudCIsInVzZXIiLCJzYWxlc21hbiJdLCJqdGkiOiI2OGM3MmI2Ny1jMWYwLTRjZDUtOTkyYy0yZjU5ZGQxYjAzOWQiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInVzZXJuYW1lIjoiaGVpbWEifQ.kk-PJzzRyAUb-hAQdX9uS5QSQl0nEp1snuUi6LsdOyFiy6eMZNr8z9rbNKxk0mJLPrPc0EgzAyrhROQo6ku22Ulrci5Su9NssGq3NyMxwDslJ1I6URm-FttNhbo70TTsH1XJ8uEeE_g22v76VtD7KA9sKxED-gBysekBPCK7-DiWukFhP3pLE_3dGp2qyUyGH0Y5Dc0XZUVOPn1hrMPG2KFwk-vXG8v6pK8n5Os8Cpqdq1_e0Ju_MaMAegv2l8zqpCfRGIB2zuaBYYLOi0jq-qMUTZM0I1Cm1vBbFO9QM7U7Fbl7zz5_An_aUyqjmHQx6GxGtiCvMjHOC1-z237abA";

    String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmnTpTCpjl6V/+ZdZ1as/LjzD7Y+pJywLLCfISmmvmNtZK571dO4S5HjxebjzxFLD/THfxN9Ap/sgowmgJP45wMiM1v9TS/V9DMiOWWBY2L7ZcyBQbANZdsExy3Ecoy3vzTbyWLbU1hySHeMn4YAa9khfxCwG+tpDH0/Xn7VGookmKQ0A9Z/O+eypUsqiqgSqBeEAplAf0EBy0t4FSSdMqZ14fy54K/9w9nlLcYoOyZi5PucEmM8cmUg0X8Y8Bonh0YjPB/tAwn5q62pEsc/haBGULwHkzHOBr54E6XfNBe6pY99K3u8F9+avJNds+ac3tnAWA8nybT9XtMwm5lipQQIDAQAB-----END PUBLIC KEY-----";

    RsaVerifier rsaVerifier = new RsaVerifier(publicKey);

    Jwt token = JwtHelper.decodeAndVerify(jwt, rsaVerifier);

    System.out.println(token.getClaims());
  }
}
