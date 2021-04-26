package com.changgou.goods.dao;

import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface BrandMapper extends Mapper<Brand> {
    @Select("SELECT * FROM tb_brand WHERE id IN \n" +
            "(SELECT brand_id FROM tb_category_brand \n" +
            " WHERE category_id IN\n" +
            " (SELECT id FROM tb_category WHERE NAME=#{categoryName}))")
    public List<Map> findBrandListByCategoryName(@Param("categoryName") String categoryName);

}
