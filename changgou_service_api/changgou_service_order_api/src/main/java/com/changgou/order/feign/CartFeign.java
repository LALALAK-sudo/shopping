package com.changgou.order.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "order")
public interface CartFeign {
    /*
    @GetMapping("/addCart")
    public Result addCart(@RequestParam("skuId")String skuId, @RequestParam("num")Integer num) {
        // 动态获取当前人的信息
        String username = "itheima";
        cartService.addCart(skuId,num,username);
        return new Result(true, StatusCode.OK,"加入购物车成功");
    }
    * */
    @GetMapping("/cart/addCart")
    public Result addCart(@RequestParam("skuId")String skuId, @RequestParam("num")Integer num);


    /*
    @GetMapping("/list")
    public Map list() {
        // 动态获取当前人信息，暂时静态
        String username = "itheima";
        Map map = cartService.list(username);
        return map;
    }
    * */
    @GetMapping("/cart/list")
    public Map list();


    }
