package com.changgou.goods.dao;

import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface SpecMapper extends Mapper<Spec> {

    @Select("SELECT NAME,OPTIONS FROM tb_spec WHERE template_id IN\n" +
            "( SELECT template_id FROM tb_category WHERE NAME=#{category})")
    public List<Map> findSpecListByCategoryName(@Param("category") String category);



}
