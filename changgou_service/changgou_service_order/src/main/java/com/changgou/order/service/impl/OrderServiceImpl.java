package com.changgou.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.dao.*;
import com.changgou.order.feign.OrderFegin;
import com.changgou.order.pojo.*;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.changgou.pay.feign.WxPayFeign;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private WxPayFeign wxPayFeign;

    /**
     * 购物车在redis 中的key的前缀
    **/
    private String CART_Prefix = "cart_";

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Order findById(String id){
        return  orderMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     * @param order
     */
    @Override
    @GlobalTransactional(name = "order_add")
    public String add(Order order) throws Exception{

        //订单id
        String id = idWorker.nextId() + "";

        //获取购物车相关数据
        Map cartMap = cartService.list(order.getUsername());
        List<OrderItem> orderItemList = (List<OrderItem>) cartMap.get("orderItemList");

        //统计计算：总金额，总商品数量
        //填充订单数据
        order.setTotalMoney((Integer) cartMap.get("totalMoney"));
        order.setTotalNum((Integer) cartMap.get("totalNum"));
        order.setPayMoney((Integer) cartMap.get("totalMoney"));
        order.setPayTime(new Date());
        order.setUpdateTime(new Date());
        order.setBuyerRate("0");//未评价
        order.setSourceType("1");//web
        order.setOrderStatus("0");//未完成 1.已完成 2.已退货
        order.setPayStatus("0");//未支付
        order.setConsignStatus("0");//未发货
        order.setId(id);

        //保存order表
        orderMapper.insertSelective(order);

        //保存item信息
        for (OrderItem item : orderItemList) {
            item.setId(idWorker.nextId()+"");
            item.setOrderId(id);
            item.setIsReturn("0");//未退货
            //保存操作
            orderItemMapper.insertSelective(item);

        }

        try {
            //扣减库存 增加销量
            skuFeign.decrCount(order.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //添加任务数据
        System.out.println("像订单数据库中的任务表添加数据");
        Task task = new Task();
        task.setId(idWorker.nextId());
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setMqExchange(RabbitMQConfig.EX_BUYING_ADDPOINTUSER);
        task.setMqRoutingkey(RabbitMQConfig.CG_BUYING_ADDPOINT_KEY);


        Map map = new HashMap();
        map.put("username",order.getUsername());
        map.put("orderId",id);
        map.put("point",order.getPayMoney());
        task.setRequestBody(JSON.toJSONString(map));

        taskMapper.insertSelective(task);

        //清空购物车redis
        redisTemplate.delete(CART_Prefix+order.getUsername());

        //发送延迟消息，订单新增完成以后，当用户迟迟未支付，则需要使用死信队列，对过期的订单消息进行关闭（过期时间即为订单保留有效期）
        rabbitTemplate.convertAndSend("",RabbitMQConfig.QUEUE_ORDERCREATE,id);

        return id;
    }


    /**
     * 修改
     * @param order
     */
    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Order> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Order>)orderMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Order> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Order>)orderMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andEqualTo("payType",searchMap.get("payType"));
           	}
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
           	}
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
           	}
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
           	}
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
           	}
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
           	}
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
           	}
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
           	}
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
           	}
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andEqualTo("sourceType",searchMap.get("sourceType"));
           	}
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
           	}
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andEqualTo("orderStatus",searchMap.get("orderStatus"));
           	}
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andEqualTo("payStatus",searchMap.get("payStatus"));
           	}
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andEqualTo("consignStatus",searchMap.get("consignStatus"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }

        }
        return example;
    }

    @Override
    public void updatePayStatus(String orderId,String transactionId) {

        //查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);

        if(order != null && "0".equals(order.getPayStatus())){
            //修改订单支付状态
            order.setPayStatus("1");
            order.setOrderStatus("1");
            order.setUpdateTime(new Date());
            order.setPayTime(new Date());
            order.setTransactionId(transactionId);

            orderMapper.updateByPrimaryKeySelective(order);

            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(orderId);
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setConsignStatus("");
            orderLog.setRemarks("交易流水号：" + transactionId);


            orderLogMapper.insertSelective(orderLog);

        }

    }

    @Override
    @Transactional
    public void closeOrder(String orderId) {
        System.out.println("关闭订单业务开启：" + orderId);

        //根据订单id查询订单信息
        Order order = orderMapper.selectByPrimaryKey(orderId);

        //判断订单是否存在
        if(order == null){
            throw new RuntimeException("关闭的订单不存在");
        }

        //判断订单支付状态
        if(!"0".equals(order.getPayStatus())){
            System.out.println("当前订单不需要关闭");
            return;
        }

        System.out.println("关闭订单校验通过：" + orderId);

        //基于微信查询订单数据
        Map wxQueryMap = (Map) wxPayFeign.queryOrder(orderId).getData();
        System.out.println("查询微信支付订单:" + wxQueryMap);

        //判断微信订单是否是已支付，进行数据补偿
        if("SUCCESSw".equals(wxQueryMap.get("trade_state"))){
            this.updatePayStatus(orderId,(String) wxQueryMap.get("transactionId"));
            System.out.println("完成数据的补偿");
        }

        //判断微信订单是否是未支付，则修改商户订单信息，新增订单日志，恢复商品库存，基于微信关闭订单
        if("NOTPAY".equals(wxQueryMap.get("trade_state"))){
            System.out.println("执行关闭");

            //商户订单状态修改
            order.setUpdateTime(new Date());
            order.setOrderStatus("4");//订单已关闭

            orderMapper.updateByPrimaryKeySelective(order);

            //新增订单日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("4");
            orderLog.setOrderId(orderId);

            orderLogMapper.insertSelective(orderLog);

            //恢复库存
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);

            List<OrderItem> itemList = orderItemMapper.select(orderItem);
            for (OrderItem item : itemList) {
                skuFeign.resumeStockNum(item.getSkuId(),item.getNum());
            }

            //基于微信关闭订单
            wxPayFeign.closeOrder(orderId);
        }
    }

    @Override
    public void batchSend(List<Order> orders) {

        //判断每一个运单号和物流公司的值是否存在
        for (Order dto : orders) {
            if(dto.getId() == null){
                throw new RuntimeException("订单号【"+ dto.getId() +"】不存在");
            }

            if(dto.getShippingCode() == null || dto.getShippingName() == null){
                throw new RuntimeException("请输入订单【"+ dto.getId() +"】的运单号或物流公司的名称");
            }

        }

        for (Order dto : orders) {
            //进行订单状态校验
            Order order = orderMapper.selectByPrimaryKey(dto.getId());

            //如果订单是已发货，或者不是未完成，则抛出异常
            if(!"0".equals(order.getConsignStatus()) || !"1".equals(order.getOrderStatus())){
                throw new RuntimeException("订单【"+ dto.getId() +"】是已发货或者不是未完成状态，不允许发货");
            }

            //修改订单状态为已发货
            order.setOrderStatus("2"); //已发货
            order.setConsignStatus("1");
            order.setConsignTime(new Date());
            order.setUpdateTime(new Date());


            orderMapper.updateByPrimaryKeySelective(order);
        }

        for (Order dto : orders) {
            //日志记录
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(dto.getId());
            orderLog.setOrderStatus("2");
            orderLog.setPayStatus("1");
            orderLog.setConsignStatus("1");
            orderLog.setRemarks("订单发货");

            orderLogMapper.insertSelective(orderLog);
        }

    }

    @Override
    @Transactional
    public void confirmTask(String orderId, String operator) {

      //修改订单状态
      Order order = orderMapper.selectByPrimaryKey(orderId);
      if(order == null){
        throw new RuntimeException("订单不存在");
      }

      if(!"1".equals(order.getConsignStatus())){
        throw new RuntimeException("订单未发货");
      }

      order.setUpdateTime(new Date());
      order.setConsignStatus("2");//已送达
      order.setEndTime(new Date());
      order.setOrderStatus("3");//已完成

      orderMapper.updateByPrimaryKeySelective(order);

      //记录日志
      OrderLog orderLog = new OrderLog();
      orderLog.setId(idWorker + "");
      orderLog.setOperater(operator);
      orderLog.setOperateTime(new Date());
      orderLog.setOrderId(orderId);
      orderLog.setOrderStatus("3");
      orderLog.setPayStatus("1");
      orderLog.setConsignStatus("2");
      orderLog.setRemarks("已确认收货");

      orderLogMapper.insertSelective(orderLog);

    }

  @Override
  public void autoTack() {

    //从订单的配置表，获取时间自动收货的期限时间
    OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey(1);

    //向前推获取的期限
    LocalDate now = LocalDate.now();
    LocalDate localDate = now.plusDays(-orderConfig.getTakeTimeout());

    //从订单表中获取相关符合条件的数据（发货时间小于过期时间，收货状态为未确认）
    Example example = new Example(Order.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("orderStatus","2");
    criteria.andLessThan("consignTime", localDate);

    List<Order> orders = orderMapper.selectByExample(example);

    //循环操作数据
    for (Order order : orders) {
      this.confirmTask(order.getId(),"system");
    }

  }
}
