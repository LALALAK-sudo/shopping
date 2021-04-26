package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map search(Map<String, String> searchMap) {
        Map<String,Object> resultMap = new HashMap<>();
        if(resultMap != null) {

            // 构建查询
            // 构建查询条件封装对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            // 按照关键字查询
            if(StringUtils.isNotEmpty(searchMap.get("keywords"))) {
                boolQuery.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")).operator(Operator.AND));
            }

            // 按照品牌进行过滤查询
            if(StringUtils.isNotEmpty(searchMap.get("brand"))) {
                boolQuery.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }

            // 按照规格经行过滤查询
            for (String key : searchMap.keySet()) {
               //System.out.println(key);
                if(key.startsWith("spec_")) {
                    String value = searchMap.get(key).replace("%2B", "+");
                    System.out.println(value);
                    boolQuery.filter(QueryBuilders.termQuery(("specMap."+key.substring(5)+".keyword"),value));
                }
            }

            // 按照价格进行区间过滤查询
            if(StringUtils.isNotEmpty(searchMap.get("price"))) {
                String[] prices = searchMap.get("price").split("-");
                // 0-500 500-1000
                if(prices.length == 2) {
                    boolQuery.filter(QueryBuilders.rangeQuery("price").lte(prices[1]));
                }
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));
            }

            nativeSearchQueryBuilder.withQuery(boolQuery);

            // 按照品牌进行分组（聚合）查询
            String skuBrand = "skuBrand";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));

            // 按照规格进行聚合查询
            String skuSpec = "skuSpec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));

            // 开启分页查询
            String pageNum = searchMap.get("pageNum");
            String pageSize = searchMap.get("pageSize");
            if(!StringUtils.isNotEmpty(pageNum)) {
                pageNum = "1";
            }
            if(!StringUtils.isNotEmpty(pageSize)) {
                pageSize="30";
            }
            // 设置分页
            // 第一个参数：当前页 是从0开始
            // 第二个参数：每页显示多少条
            nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize)));

            // 按照相关字段进行排序
            // 1 当前域 2 当前的排序操作（升序ASC  降序DESC）
            if(StringUtils.isNotEmpty(searchMap.get("sortField")) && StringUtils.isNotEmpty(searchMap.get("sortRule"))) {
                if("ASC".equals(searchMap.get("sortRule"))) {
                    // 升序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort((searchMap.get("sortField"))).order(SortOrder.ASC));
                }else {
                    // 降序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort((searchMap.get("sortField"))).order(SortOrder.DESC));
                }
            }

            // 设置高亮域以及高亮的样式
            HighlightBuilder.Field field = new HighlightBuilder.Field("name") // 高亮域
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");
            nativeSearchQueryBuilder.withHighlightFields(field);
            // 开启查询
            /**SearchQuery var1, Class<T> var2, SearchResultMapper var3
             * 第一个参数：条件构建对象
             * 第二个参数：查询操作对象
             * 第三个参数：查询结果操作对象
             */
            AggregatedPage<SkuInfo> resultInfo = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class,
                    new SearchResultMapper() {
                        @Override
                        public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                            // 查询结果操作
                            List<T> list = new ArrayList<>();
                            // 获取查询命中结果数据
                            SearchHits hits = searchResponse.getHits();
                            if (hits != null) {
                                // 有查询结果
                                for (SearchHit hit : hits) {
                                    // SearchHit转换为skuInfo
                                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                                    if(highlightFields != null && highlightFields.size()>0) {
                                        // 替换数据
                                        // System.out.println(highlightFields.get("name").getFragments()[0].toString());
                                        skuInfo.setName(highlightFields.get("name").getFragments()[0].toString());
                                    }
                                    list.add((T) skuInfo);
                                }
                            }
                            return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), searchResponse.getAggregations());
                        }
                    });
            // 封装查询结果
            // 总记录数
            resultMap.put("total",resultInfo.getTotalElements());
            // 总页数
            resultMap.put("totalPages",resultInfo.getTotalPages());
            // 数据集合
            resultMap.put("rows",resultInfo.getContent());

            // 封装品牌的分组结果
            StringTerms brandTerms = (StringTerms) resultInfo.getAggregation(skuBrand);
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList",brandList);

            // 封装规格分组结果
            StringTerms specTerms = (StringTerms) resultInfo.getAggregation(skuSpec);
            // System.out.println(specTerms);
            List<String> specList = specTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("specList",specList);

            resultMap.put("specList",this.formatSpec(specList));

            // 当前页
            resultMap.put("pageNum",pageNum);
            return resultMap;
        }
        return null;
    }

//     "specList": [
//             "{}",
//             "{'颜色': '黑色', '尺码': '平光防蓝光-无度数电脑手机护目镜'}",
//             "{'颜色': '红色', '尺码': '150度'}",
//             "{'颜色': '黑色', '尺码': '150度'}",
//             "{'颜色': '黑色'}",
//             "{'颜色': '红色', '尺码': '100度'}",
//             "{'颜色': '红色', '尺码': '250度'}",
//             "{'颜色': '红色', '尺码': '350度'}",
//             "{'颜色': '黑色', '尺码': '200度'}",
//             "{'颜色': '黑色', '尺码': '250度'}"
//             ],

    /*
    需要的数据
    {
        颜色：[黑色，红色]
        尺码：[100度，150度]
    }
    * */
    public Map<String, Set<String>> formatSpec(List<String> specList) {
        Map<String,Set<String>> resultMap = new HashMap<>();
        if(specList != null && specList.size()>0) {
            for (String specJsonString : specList) {
                // json数据转换为map
                Map<String,String> specMap = JSON.parseObject(specJsonString, Map.class);
                for (String specKey : specMap.keySet()) {
                    Set<String> specSet = resultMap.get(specKey);
                    if(specSet == null) {
                        specSet = new HashSet<>();
                    }
                    specSet.add(specMap.get(specKey));
                    resultMap.put(specKey,specSet);
                }
            }
        }
        return resultMap;
    }

}
