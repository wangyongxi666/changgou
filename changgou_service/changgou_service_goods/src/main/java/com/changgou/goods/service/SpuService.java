package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Spu findById(String id);

    /***
     * 新增
     * @param spu
     */
    void add(Goods goods);

    /***
     * 修改
     * @param spu
     */
    void update(Goods goods);

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
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);

    /**
     * @Author YongXi.Wang
     * @Description 根据id查询goods
     * @Date 2020/2/19 18:30
     * @Param [id]
     * @return com.changgou.goods.pojo.Goods
     **/
    Goods findGoodsById(String id);

    /**
     * @Author YongXi.Wang
     * @Description 商品审核并自动上架
     * @Date 2020/2/19 20:16
     * @Param [id]
     * @return void
    **/
    void auth(String id);

    /**
     * @Author YongXi.Wang
     * @Description 下架
     * @Date 2020/2/19 20:16
     * @Param [id]
     * @return void
    **/
    void pull(String id);

    /**
     * @Author YongXi.Wang
     * @Description 上架
     * @Date 2020/2/19 20:16
     * @Param [id]
     * @return void
    **/
    void put(String id);

    /**
     * @Author YongXi.Wang
     * @Description 还原删除
     * @Date 2020/2/19 23:41
     * @Param [id]
     * @return void
    **/
    void resotre(String id);

    /**
     * @Author YongXi.Wang
     * @Description 物理删除
     * @Date 2020/2/19 23:46
     * @Param [id]
     * @return void
    **/
    void realDel(String id);
}
