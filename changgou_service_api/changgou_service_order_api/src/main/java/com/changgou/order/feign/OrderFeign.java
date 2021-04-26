package com.changgou.order.feign;

import com.changgou.entity.Result;
import com.changgou.order.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order")
public interface OrderFeign {
    /*
    @PostMapping
    public Result add(@RequestBody Order order){
        // 获取登陆人名称
        String username = tokenDecode.getUserInfo().get("username");
        order.setUsername(username);
        orderService.add(order);
        return new Result(true,StatusCode.OK,"添加成功");
    }
    * */
    @PostMapping("/order")
    public Result add(@RequestBody Order order);

    /*
        @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Order order = orderService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",order);
    }
    * */
    @GetMapping("/order/{id}")
    public Result<Order> findById(@PathVariable String id);

}
