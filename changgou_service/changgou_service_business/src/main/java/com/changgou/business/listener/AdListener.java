package com.changgou.business.listener;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName AdListener
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月20日 22:32
 * @Version 1.0.0
*/
@Component
public class AdListener {

  @RabbitListener(queues = "ad_update_queue")
  public void receiveMessage(String message){

    System.out.println("接收到的数据为:" + message);

    OkHttpClient okHttpClient = new OkHttpClient();

    //发起远程调用
    String url = "http://192.168.0.101/ad_update?position=" + message;

    Request request = new Request.Builder().url(url).build();

    Call call = okHttpClient.newCall(request);

    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        //请求成功
        System.out.println("请求成功" + response.message());
      }
    });

  }



}
