package com.changgou.order.service;

import com.changgou.order.pojo.Order;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /***
     * 查询所有
     * @return
     */
    List<Order> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Order findById(String id);

    /***
     * 新增
     * @param order
     */
    String add(Order order) throws Exception;

    /***
     * 修改
     * @param order
     */
    void update(Order order);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Order> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Order> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Order> findPage(Map<String, Object> searchMap, int page, int size);


    /**
     * @Author YongXi.Wang
     * @Description 修改对应订单的支付状态 并记录日志
     * @Date 2020/3/1 18:44
     * @Param [orderId]
     * @return void
    **/
  void updatePayStatus(String orderId,String transactionId);

  /**
   * @Author YongXi.Wang
   * @Description 关闭订单
   * @Date 2020/3/2 0:54
   * @Param [orderId]
   * @return void
  **/
  void closeOrder(String orderId);
  
  /**
   * @Author YongXi.Wang
   * @Description  批量发货
   * @Date 2020/3/2 12:15
   * @Param [] 
   * @return void
  **/
  void batchSend(List<Order> orders);

  /**
   * @Author YongXi.Wang
   * @Description 手动确认收货
   * @Date 2020/3/2 14:15
   * @Param [orderId, operator]
   * @return void
  **/
  void confirmTask(String orderId,String operator);

  /**
   * @Author YongXi.Wang
   * @Description 自动收货
   * @Date 2020/3/2 15:10
   * @Param []
   * @return void
  **/
  void autoTack();
}
