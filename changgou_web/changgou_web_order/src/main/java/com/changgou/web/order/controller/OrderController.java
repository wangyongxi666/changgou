package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.order.feign.AddressFegin;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.feign.OrderFegin;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月26日 19:47
 * @Version 1.0.0
*/
@Controller
@RequestMapping("/worder")
public class OrderController {

  @Autowired
  private AddressFegin addressFegin;

  @Autowired
  private CartFeign cartFeign;

  @Autowired
  private OrderFegin orderFegin;

  @GetMapping("/order")
  public String readOrder(Model model){

    //获取收货地址信息
    List<Address> addressList = addressFegin.list().getData();

    //购物车信息
    Map map = cartFeign.list();
    List<OrderItem> orderItemList = (List<OrderItem>) map.get("orderItemList");
    Integer totalMoney = (Integer) map.get("totalMoney");
    Integer totalNum = (Integer) map.get("totalNum");

    //封装数据
    model.addAttribute("address",addressList);
    model.addAttribute("carts",orderItemList);
    model.addAttribute("totalMoney",totalMoney);
    model.addAttribute("totalNum",totalNum);

    //默认收件人信息
    for (Address address : addressList) {
      if("1".equals(address.getIsDefault())){
        model.addAttribute("deAddr",address);
        break;
      }
    }

    return "order";
  }

  @PostMapping("/add")
  @ResponseBody
  public Result add(@RequestBody Order order){

    Result result = orderFegin.add(order);

    return result;
  }

  @GetMapping("/toPayPage")
  public String toPayPage(String orderId,Model model){

    //获取订单相关信息
    Result<Order> result = orderFegin.findById(orderId);
    System.out.println(result);
    Order order = result.getData();

    model.addAttribute("payMoney",order.getTotalMoney());
    model.addAttribute("orderId",orderId);

    return "pay";
  }

}
