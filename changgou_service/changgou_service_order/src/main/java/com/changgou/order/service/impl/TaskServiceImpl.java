package com.changgou.order.service.impl;

import com.changgou.order.dao.TaskHisMapper;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.Task;
import com.changgou.order.pojo.TaskHis;
import com.changgou.order.service.TaskService;
import com.changgou.util.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @ClassName TaskServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年02月28日 23:48
 * @Version 1.0.0
*/
@Service
public class TaskServiceImpl implements TaskService{

  @Autowired
  private TaskHisMapper taskHisMapper;

  @Autowired
  private TaskMapper taskMapper;

  @Autowired
  private IdWorker idWorker;

  @Override
  @Transactional
  public int delTask(Task task) {

    //记录删除时间
    task.setDeleteTime(new Date());
    task.setUpdateTime(new Date());
    Long id = task.getId();

    //bean拷贝
    TaskHis taskHis = new TaskHis();
    BeanUtils.copyProperties(task,taskHis);

    taskHis.setId(idWorker.nextId());

    //记录历史任务数据
    int insCount = taskHisMapper.insertSelective(taskHis);
    if(insCount <= 0){
      return 0;
    }

    //删除原任务数据
    int delCount = taskMapper.deleteByPrimaryKey(task);
    if(delCount <= 0){
      return 0;
    }

    System.out.println("订单服务完成了添加历史任务并删除原任务的操作");

    return 1;
  }

}
