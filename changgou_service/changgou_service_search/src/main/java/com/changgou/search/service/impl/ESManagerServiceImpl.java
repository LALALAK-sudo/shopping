package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ESManagerServiceImpl implements ESManagerService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ESManagerMapper esManagerMapper;

    @Override
    public void createMappingAndIndex() {
        // 创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        // 创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    // 导入全部sku集合进入索引库
    @Override
    public void importAll() {
        // 查询sku集合
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        if(skuList == null && skuList.size() == 0) {
            throw new RuntimeException("当前没有数据被查询到，无法导入索引库");
        }

        // skuList转换为JSON
        String jsonSkuList = JSON.toJSONString(skuList);
        // 将json转换为skuInfo
        List<SkuInfo> skuInfos = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfos) {
            //System.out.println(skuInfo.getSpecMap().toString());
            // 将规格信息转换为map
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }

        // 导入索引库
        esManagerMapper.saveAll(skuInfos);
    }


    // 根据spuId查询skuList集合， 添加到索引库
    @Override
    public void importDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if(skuList == null && skuList.size() == 0) {
            throw new RuntimeException("当前没有对应spuId被查询到，导入失败");
        }
        // 转为JSON
        String jsonString = JSON.toJSONString(skuList);
        // 转换为skuInfo
        List<SkuInfo> skuInfos = JSON.parseArray(jsonString, SkuInfo.class);

        // 规格转换为map ，存入skuInfo
        for (SkuInfo skuInfo : skuInfos) {
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        esManagerMapper.saveAll(skuInfos);
    }

    @Override
    public void delDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if(skuList == null && skuList.size() == 0) {
            throw new RuntimeException("当前没有数据被查询到，无法导入索引库");
        }
        for (Sku sku : skuList) {
            esManagerMapper.deleteById(Long.parseLong(sku.getId()));
        }
    }
}
