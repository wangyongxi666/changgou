package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

  @Autowired
  private SpuMapper spuMapper;

  @Autowired
  private SkuMapper skuMapper;

  @Autowired
  private IdWorker idWorker;

  @Autowired
  private CategoryMapper categoryMapper;

  @Autowired
  private BrandMapper brandMapper;

  @Autowired
  private CategoryBrandMapper categoryBrandMapper;

  /**
   * 查询全部列表
   *
   * @return
   */
  @Override
  public List<Spu> findAll() {
    return spuMapper.selectAll();
  }

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  @Override
  public Spu findById(String id) {
    return spuMapper.selectByPrimaryKey(id);
  }


  /**
   * 增加
   *
   * @param goods
   */
  @Override
  @Transactional
  public void add(Goods goods) {
    //添加spu
    Spu spu = goods.getSpu();

    //设置id
    long id = idWorker.nextId();
    spu.setId(String.valueOf(id));

    //设置删除状态
    spu.setIsDelete("0");

    //设置上架状态
    spu.setIsMarketable("0");

    //设置审核状态
    spu.setStatus("0");

    spuMapper.insertSelective(spu);

    //添加sku集合
    this.saveSkuList(goods);
  }

  private void saveSkuList(Goods goods) {

    List<Sku> skuList = goods.getSkuList();
    Spu spu = goods.getSpu();

    Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());

    Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());

    //查询分类与品牌的关联关系
    CategoryBrand categoryBrand = new CategoryBrand();
    categoryBrand.setBrandId(spu.getBrandId());
    categoryBrand.setCategoryId(spu.getCategory3Id());
    int count = categoryBrandMapper.selectCount(categoryBrand);
    //如果没有查询出数据，证明两个数据还没有建立关联
    if (count == 0) {
      categoryBrandMapper.insertSelective(categoryBrand);
    }

    if (skuList != null && skuList.size() > 0) {

      //循环skuList
      for (Sku sku : skuList) {

        //设置sku主键
        sku.setId(String.valueOf(idWorker.nextId()));
        //设置spu关联id
        sku.setSpuId(spu.getId());

        //设置sku规格
        String spec = sku.getSpec();
        if (spec == null || spec.isEmpty()) {
          sku.setSpec("{}");
        }

        //设置规格名称 spu名称 +规格
        String spuName = spu.getName();
        //转换json
        Map map = JSON.parseObject(spec, Map.class);
        if (map != null && map.size() > 0) {
          Collection values = map.values();
          for (Object value : values) {
            spuName += " " + value;
          }
        }
        sku.setName(spuName);

        //设置品牌名称
        sku.setBrandName(brand.getName());
        //设置分类名称
        sku.setCategoryName(category.getName());
        sku.setCategoryId(category.getId());
        //创建日期
        sku.setCreateTime(new Date());
        //修改日期
        sku.setUpdateTime(new Date());
        //插入数据库
        skuMapper.insertSelective(sku);

      }
    }

  }


  /**
   * 修改
   *
   * @param goods
   */
  @Override
  public void update(Goods goods) {
    //修改spu
    Spu spu = goods.getSpu();
    spuMapper.updateByPrimaryKey(spu);

    //修改sku
    //删除旧sku数据
    Example example = new Example(Sku.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("spuId", spu.getId());
    skuMapper.deleteByExample(example);

    this.saveSkuList(goods);
  }

  /**
   * 删除
   *
   * @param id
   */
  @Override
  @Transactional
  public void delete(String id) {
    //查询spu
    Spu spu = spuMapper.selectByPrimaryKey(id);
    if (!spu.getIsMarketable().equals("0")) {
      throw new RuntimeException("当前商品必须是下架状态才能删除");
    }
    spu.setIsDelete("1");
    spu.setStatus("0");
    spuMapper.updateByPrimaryKeySelective(spu);
  }


  /**
   * 条件查询
   *
   * @param searchMap
   * @return
   */
  @Override
  public List<Spu> findList(Map<String, Object> searchMap) {
    Example example = createExample(searchMap);
    return spuMapper.selectByExample(example);
  }

  /**
   * 分页查询
   *
   * @param page
   * @param size
   * @return
   */
  @Override
  public Page<Spu> findPage(int page, int size) {
    PageHelper.startPage(page, size);
    return (Page<Spu>) spuMapper.selectAll();
  }

  /**
   * 条件+分页查询
   *
   * @param searchMap 查询条件
   * @param page      页码
   * @param size      页大小
   * @return 分页结果
   */
  @Override
  public Page<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
    PageHelper.startPage(page, size);
    Example example = createExample(searchMap);
    return (Page<Spu>) spuMapper.selectByExample(example);
  }

  /**
   * 构建查询对象
   *
   * @param searchMap
   * @return
   */
  private Example createExample(Map<String, Object> searchMap) {
    Example example = new Example(Spu.class);
    Example.Criteria criteria = example.createCriteria();
    if (searchMap != null) {
      // 主键
      if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
        criteria.andEqualTo("id", searchMap.get("id"));
      }
      // 货号
      if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
        criteria.andEqualTo("sn", searchMap.get("sn"));
      }
      // SPU名
      if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
        criteria.andLike("name", "%" + searchMap.get("name") + "%");
      }
      // 副标题
      if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
        criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
      }
      // 图片
      if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
        criteria.andLike("image", "%" + searchMap.get("image") + "%");
      }
      // 图片列表
      if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
        criteria.andLike("images", "%" + searchMap.get("images") + "%");
      }
      // 售后服务
      if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
        criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
      }
      // 介绍
      if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
        criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
      }
      // 规格列表
      if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
        criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
      }
      // 参数列表
      if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
        criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
      }
      // 是否上架
      if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
        criteria.andEqualTo("isMarketable", searchMap.get("isMarketable"));
      }
      // 是否启用规格
      if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
        criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
      }
      // 是否删除
      if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
        criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
      }
      // 审核状态
      if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
        criteria.andEqualTo("status", searchMap.get("status"));
      }

      // 品牌ID
      if (searchMap.get("brandId") != null) {
        criteria.andEqualTo("brandId", searchMap.get("brandId"));
      }
      // 一级分类
      if (searchMap.get("category1Id") != null) {
        criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
      }
      // 二级分类
      if (searchMap.get("category2Id") != null) {
        criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
      }
      // 三级分类
      if (searchMap.get("category3Id") != null) {
        criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
      }
      // 模板ID
      if (searchMap.get("templateId") != null) {
        criteria.andEqualTo("templateId", searchMap.get("templateId"));
      }
      // 运费模板id
      if (searchMap.get("freightId") != null) {
        criteria.andEqualTo("freightId", searchMap.get("freightId"));
      }
      // 销量
      if (searchMap.get("saleNum") != null) {
        criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
      }
      // 评论数
      if (searchMap.get("commentNum") != null) {
        criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
      }

    }
    return example;
  }

  @Override
  public Goods findGoodsById(String id) {

    Goods goods = new Goods();

    //封装spu
    Spu spu = spuMapper.selectByPrimaryKey(id);
    goods.setSpu(spu);

    //封装sku
    Example example = new Example(Sku.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("spuId", spu.getId());

    List<Sku> skuList = skuMapper.selectByExample(example);
    goods.setSkuList(skuList);

    return goods;
  }

  @Override
  @Transactional
  public void auth(String id) {

    //查询sku
    Spu spu = spuMapper.selectByPrimaryKey(id);

    //判断sku是否已经删除
    if (spu == null) {
      throw new RuntimeException("当前商品不存在");
    }

    //审核 上架状态都为1
    if ("1".equals(spu.getIsDelete())) {
      throw new RuntimeException("当前商品处于删除状态");
    }

    //修改
    spu.setStatus("1");
    spu.setIsMarketable("1");
    spuMapper.updateByPrimaryKey(spu);
  }

  @Override
  @Transactional
  public void pull(String id) {

    //查询sku
    Spu spu = spuMapper.selectByPrimaryKey(id);

    //判断sku是否已经删除
    if (spu == null) {
      throw new RuntimeException("当前商品不存在");
    }

    //审核 上架状态都为1
    if ("1".equals(spu.getIsDelete())) {
      throw new RuntimeException("当前商品处于删除状态");
    }

    //修改
    spu.setIsMarketable("0");
    spuMapper.updateByPrimaryKey(spu);
  }

  @Override
  @Transactional
  public void put(String id) {

    //查询sku
    Spu spu = spuMapper.selectByPrimaryKey(id);

    //判断sku是否已经删除
    if (spu == null) {
      throw new RuntimeException("当前商品不存在");
    }

    //审核 上架状态都为1
    if ("1".equals(spu.getIsDelete())) {
      throw new RuntimeException("当前商品处于删除状态");
    }

    //审核 上架状态都为1
    if ("0".equals(spu.getStatus())) {
      throw new RuntimeException("当前商品处于未审核状态");
    }

    //修改
    spu.setIsMarketable("1");
    spuMapper.updateByPrimaryKey(spu);
  }

  @Override
  @Transactional
  public void resotre(String id) {

    //查询spu
    Spu spu = spuMapper.selectByPrimaryKey(id);

    if (!spu.getIsDelete().equals("1")) {
      throw new RuntimeException("该商品不是删除状态，不允许还原");
    }

    spu.setIsDelete("0");
    spuMapper.updateByPrimaryKey(spu);
  }

  @Override
  @Transactional
  public void realDel(String id) {

    //查询spu
    Spu spu = spuMapper.selectByPrimaryKey(id);

    if (!spu.getIsDelete().equals("1")) {
      throw new RuntimeException("该商品不是删除状态，不允许物理删除");
    }

    spuMapper.deleteByPrimaryKey(spu);
  }


}
