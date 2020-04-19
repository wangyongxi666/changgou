package com.changgou.goods.controller;
import com.changgou.entity.PageResult;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.service.SpuService;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/spu")
public class SpuController {


    @Autowired
    private SpuService spuService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Spu> spuList = spuService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",spuList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Goods goods = spuService.findGoodsById(id);
        return new Result(true,StatusCode.OK,"查询成功",goods);
    }

    @GetMapping("/findSpuById/{id}")
    public Result<Spu> findSpuById(@PathVariable String id){
        Spu spu = spuService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",spu);
    }


    /***
     * 新增数据
     * @param goods
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Goods goods){
        spuService.add(goods);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param goods
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Goods goods,@PathVariable String id){
        spuService.update(goods);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        spuService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Spu> list = spuService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Spu> pageList = spuService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * @Author YongXi.Wang
     * @Description 商品审核并上架
     * @Date 2020/2/19 20:21
     * @Param [id]
     * @return com.changgou.entity.Result
    **/
    @PutMapping("/audit/{id}")
    public Result audit(@PathVariable String id){
        spuService.auth(id);
        return new Result<>(true,StatusCode.OK,"审核成功");
    }

    /**
     * @Author YongXi.Wang
     * @Description 下架
     * @Date 2020/2/19 20:21
     * @Param [id]
     * @return com.changgou.entity.Result
    **/
    @PutMapping("/pull/{id}")
    public Result pull(@PathVariable String id){
        spuService.pull(id);
        return new Result<>(true,StatusCode.OK,"下架成功");
    }

    /**
     * @Author YongXi.Wang
     * @Description 上架
     * @Date 2020/2/19 20:21
     * @Param [id]
     * @return com.changgou.entity.Result
    **/
    @PutMapping("/put/{id}")
    public Result put(@PathVariable String id){
        spuService.put(id);
        return new Result<>(true,StatusCode.OK,"上架成功");
    }

    /**
     * @Author YongXi.Wang
     * @Description 还原删除商品
     * @Date 2020/2/19 20:21
     * @Param [id]
     * @return com.changgou.entity.Result
    **/
    @PutMapping("/resotre/{id}")
    public Result resotre(@PathVariable String id){
        spuService.resotre(id);
        return new Result<>(true,StatusCode.OK,"还原商品成功");
    }

    /**
     * @Author YongXi.Wang
     * @Description 还原删除商品
     * @Date 2020/2/19 20:21
     * @Param [id]
     * @return com.changgou.entity.Result
    **/
    @PutMapping("/realDel/{id}")
    public Result realDel(@PathVariable String id){
        spuService.realDel(id);
        return new Result<>(true,StatusCode.OK,"删除商品成功");
    }


}
