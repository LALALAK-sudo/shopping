package com.changgou.search.dao;


import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//通用的Elasticsearch的方法
public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
