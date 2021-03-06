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

            // ????????????
            // ??????????????????????????????
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            // ?????????????????????
            if(StringUtils.isNotEmpty(searchMap.get("keywords"))) {
                boolQuery.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")).operator(Operator.AND));
            }

            // ??????????????????????????????
            if(StringUtils.isNotEmpty(searchMap.get("brand"))) {
                boolQuery.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }

            // ??????????????????????????????
            for (String key : searchMap.keySet()) {
               //System.out.println(key);
                if(key.startsWith("spec_")) {
                    String value = searchMap.get(key).replace("%2B", "+");
                    System.out.println(value);
                    boolQuery.filter(QueryBuilders.termQuery(("specMap."+key.substring(5)+".keyword"),value));
                }
            }

            // ????????????????????????????????????
            if(StringUtils.isNotEmpty(searchMap.get("price"))) {
                String[] prices = searchMap.get("price").split("-");
                // 0-500 500-1000
                if(prices.length == 2) {
                    boolQuery.filter(QueryBuilders.rangeQuery("price").lte(prices[1]));
                }
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));
            }

            nativeSearchQueryBuilder.withQuery(boolQuery);

            // ??????????????????????????????????????????
            String skuBrand = "skuBrand";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));

            // ??????????????????????????????
            String skuSpec = "skuSpec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));

            // ??????????????????
            String pageNum = searchMap.get("pageNum");
            String pageSize = searchMap.get("pageSize");
            if(!StringUtils.isNotEmpty(pageNum)) {
                pageNum = "1";
            }
            if(!StringUtils.isNotEmpty(pageSize)) {
                pageSize="30";
            }
            // ????????????
            // ??????????????????????????? ??????0??????
            // ???????????????????????????????????????
            nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.parseInt(pageNum)-1,Integer.parseInt(pageSize)));

            // ??????????????????????????????
            // 1 ????????? 2 ??????????????????????????????ASC  ??????DESC???
            if(StringUtils.isNotEmpty(searchMap.get("sortField")) && StringUtils.isNotEmpty(searchMap.get("sortRule"))) {
                if("ASC".equals(searchMap.get("sortRule"))) {
                    // ??????
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort((searchMap.get("sortField"))).order(SortOrder.ASC));
                }else {
                    // ??????
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort((searchMap.get("sortField"))).order(SortOrder.DESC));
                }
            }

            // ????????????????????????????????????
            HighlightBuilder.Field field = new HighlightBuilder.Field("name") // ?????????
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");
            nativeSearchQueryBuilder.withHighlightFields(field);
            // ????????????
            /**SearchQuery var1, Class<T> var2, SearchResultMapper var3
             * ????????????????????????????????????
             * ????????????????????????????????????
             * ??????????????????????????????????????????
             */
            AggregatedPage<SkuInfo> resultInfo = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class,
                    new SearchResultMapper() {
                        @Override
                        public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                            // ??????????????????
                            List<T> list = new ArrayList<>();
                            // ??????????????????????????????
                            SearchHits hits = searchResponse.getHits();
                            if (hits != null) {
                                // ???????????????
                                for (SearchHit hit : hits) {
                                    // SearchHit?????????skuInfo
                                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                                    if(highlightFields != null && highlightFields.size()>0) {
                                        // ????????????
                                        // System.out.println(highlightFields.get("name").getFragments()[0].toString());
                                        skuInfo.setName(highlightFields.get("name").getFragments()[0].toString());
                                    }
                                    list.add((T) skuInfo);
                                }
                            }
                            return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), searchResponse.getAggregations());
                        }
                    });
            // ??????????????????
            // ????????????
            resultMap.put("total",resultInfo.getTotalElements());
            // ?????????
            resultMap.put("totalPages",resultInfo.getTotalPages());
            // ????????????
            resultMap.put("rows",resultInfo.getContent());

            // ???????????????????????????
            StringTerms brandTerms = (StringTerms) resultInfo.getAggregation(skuBrand);
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList",brandList);

            // ????????????????????????
            StringTerms specTerms = (StringTerms) resultInfo.getAggregation(skuSpec);
            // System.out.println(specTerms);
            List<String> specList = specTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("specList",specList);

            resultMap.put("specList",this.formatSpec(specList));

            // ?????????
            resultMap.put("pageNum",pageNum);
            return resultMap;
        }
        return null;
    }

//     "specList": [
//             "{}",
//             "{'??????': '??????', '??????': '???????????????-??????????????????????????????'}",
//             "{'??????': '??????', '??????': '150???'}",
//             "{'??????': '??????', '??????': '150???'}",
//             "{'??????': '??????'}",
//             "{'??????': '??????', '??????': '100???'}",
//             "{'??????': '??????', '??????': '250???'}",
//             "{'??????': '??????', '??????': '350???'}",
//             "{'??????': '??????', '??????': '200???'}",
//             "{'??????': '??????', '??????': '250???'}"
//             ],

    /*
    ???????????????
    {
        ?????????[???????????????]
        ?????????[100??????150???]
    }
    * */
    public Map<String, Set<String>> formatSpec(List<String> specList) {
        Map<String,Set<String>> resultMap = new HashMap<>();
        if(specList != null && specList.size()>0) {
            for (String specJsonString : specList) {
                // json???????????????map
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
