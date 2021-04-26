package com.changgou.goods.feign;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")
public interface CategoryFeign {
//    @GetMapping("/{id}")
//    public Result findById(@PathVariable Integer id){
//        Category category = categoryService.findById(id);
//        return new Result(true, StatusCode.OK,"查询成功",category);
//    }
    @GetMapping("/category/{id}")
    public Result<Category> findById(@PathVariable Integer id);
}
