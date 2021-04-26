package com.changgou.user.feign;

import com.changgou.entity.Result;
import com.changgou.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user")
public interface AddressFeign {
    /*
     @GetMapping("/list")
    public Result<List<Address>> list() {
        // 获取当前的登陆人名称
        String username = tokenDecode.getUserInfo().get("username");
        // 查询登录人相关的收件人地址信息
        List<Address> addressList = addressService.list(username);
        return new Result<>(true,StatusCode.OK,"查询成功",addressList);
    }
    * */
    @GetMapping("/address/list")
    public Result<List<Address>> list();
}
