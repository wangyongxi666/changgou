package com.changgou.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @ClassName FeignInterceptor
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月26日 12:50
 * @Version 1.0.0
*/
@Component
public class FeignInterceptor implements RequestInterceptor{

  @Override
  public void apply(RequestTemplate requestTemplate) {

    //获取头信息 Authorization jwt
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if(requestAttributes != null){
      HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
      if(request != null){
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
          String headerName = headerNames.nextElement();
          if("Authorization".equalsIgnoreCase(headerName)){
            String header = request.getHeader(headerName);// Bearer jwt

            //传递令牌
            requestTemplate.header(headerName,header);
          }
        }
      }
    }


  }

}
