package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
@RequestMapping("/sku")
public class SkuController


 @GetMapping("/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId) {
        Map<String, Object> searchMap = new HashMap<>();
        if(!"all".equals(spuId)) {
            searchMap.put("spuId",spuId);
        }
        searchMap.put("status","1");
        List<Sku> list = skuService.findList(searchMap);
        return list;
    }
 */

@FeignClient(name = "goods")
public interface SkuFeign {
    @GetMapping("/sku/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    /*
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Sku sku = skuService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",sku);
    }
    * */
    @GetMapping("/sku/{id}")
    public Result<Sku> findById(@PathVariable String id);

    /*
    @PostMapping("/decr/count")
    public Result decrCount(@RequestParam("username") String username) {
        skuService.decrCount(username);
        return new Result(true,StatusCode.OK,"库存扣减成功");
    }
    * */
    @PostMapping("/sku/decr/count")
    public Result decrCount(@RequestParam("username") String username);

    /*
    @RequestMapping("/resumeStockNum")
    public Result resumeStockNum(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num) {
        skuService.resumeStockNum(skuId,num);
        return new Result(true,StatusCode.OK,"回滚事务成功");
    }
    * */
    @RequestMapping("/sku/resumeStockNum")
    public Result resumeStockNum(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num);

    }
