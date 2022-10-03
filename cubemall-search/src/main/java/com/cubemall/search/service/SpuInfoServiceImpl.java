package com.cubemall.search.service;

import com.cubemall.search.dao.SpuInfoDao;
import com.cubemall.search.model.SpuInfo;
import com.cubemall.search.repository.SpuInfoRepository;
import com.kkb.cubemall.common.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpuInfoServiceImpl implements SpuInfoService {
    @Autowired
    private SpuInfoDao spuInfoDao;
    /**
     * ES底层帮忙实现一个代理对象
     */
    @Autowired
    private SpuInfoRepository spuInfoRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public R getSpuInfo(Long spuId) {
        //用户在前端点击上架商品
        //从数据库查询spu
        SpuInfo newSpuInformation = spuInfoDao.getNewSpuInformation(spuId);
        //同步到ES中
        SpuInfo spuInfo = spuInfoRepository.save(newSpuInformation);
        return R.ok().put("data",spuInfo);
    }

    @Override
    public R getAllSpuInfo() {
        //1.查询所有需要索引的SPU信息
        List<SpuInfo> allSpuInformation = spuInfoDao.getAllSpuInformation();
        //2.导入到ES索引库
        spuInfoRepository.saveAll(allSpuInformation);
        return R.ok();
    }

    @Override
    public Map<String, Object> search(Map<String, String> paramMap) throws IOException {
    //1）接收controller传递的参数
    //2）跟参数封装查询条件

    //  根据关键词查询
    //  品牌过滤
    //  分类过滤
    //  价格区间过滤
    //   排序
    //   分页
    //   高亮
    //  聚合条件：品牌、分类
    NativeSearchQuery query = buildQuery(paramMap);
    //   3）执行查询
    SearchHits<SpuInfo> searchHits = elasticsearchRestTemplate.search(query, SpuInfo.class);
    //取返回值结果
    Map<String, Object> resultMap = parseResponse(searchHits);
    //封装需要回显的page信息
    //获取总记录数
    long totalHits = searchHits.getTotalHits();
    resultMap.put("totalRows",totalHits );

    //当前页 默认设置为1
    String pageNumString = paramMap.get("pageNum");
    int pageNum = StringUtils.isNotBlank(pageNumString)?Integer.parseInt(pageNumString):1;
    resultMap.put("pageNum",pageNum);
    //总页数
    int totalPages =(int)Math.ceil(totalHits / 60);
    resultMap.put("total",totalPages);
    return resultMap;
    }


    private NativeSearchQuery buildQuery(Map<String, String> paramMap) throws IOException {
        String keywords = paramMap.get("keywords");
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //keywords必须包含
        if(StringUtils.isEmpty(keywords)){
            keywords = "";
        }
        queryBuilder.withQuery(QueryBuilders.matchQuery("spuName",keywords));
        //品牌过滤
        String brand = paramMap.get("brand");
        if(StringUtils.isNotBlank(brand)){
            queryBuilder.withFilter(QueryBuilders.termQuery("brandName",brand));
        }
        //分类过滤
        String category = paramMap.get("category");
        if(StringUtils.isNotBlank(category)){
            queryBuilder.withFilter(QueryBuilders.termQuery("categoryName",category));
        }
        //价格区间过滤
        String[] prices = paramMap.get("price").split("-");
        if(prices.length > 0){
            queryBuilder.withFilter(QueryBuilders.rangeQuery("price").gte(prices[0]).lte(prices[1]));
        }
        //排序
        String sortRule = paramMap.get("sortRule");
        String sortField = paramMap.get("sortField");
        if(StringUtils.isNotBlank(sortRule) && StringUtils.isNotBlank(sortField)){
            if("ASC".equals(sortRule)){
                queryBuilder.withSort(SortBuilders.fieldSort("sortField").order(SortOrder.ASC));
            }else{
                queryBuilder.withSort(SortBuilders.fieldSort("sortField").order(SortOrder.DESC));
            }
        }
       //分页
        String pageNumString = paramMap.get("pageNum");
        int pageNum = 1;

        if(StringUtils.isNotBlank(pageNumString)){
            pageNum = Integer.parseInt(pageNumString);
        }
        queryBuilder.withPageable(PageRequest.of(pageNum,60));

        //高亮
        queryBuilder.withHighlightBuilder(new HighlightBuilder().field("spuName").preTags("<em style=\"color:red\">").postTags("</em>"));
        //聚合 brand category
        queryBuilder.addAggregation(AggregationBuilders.terms("brandGroup").field("brand"));
        queryBuilder.addAggregation(AggregationBuilders.terms("categoryGroup").field("category"));
        return queryBuilder.build();
    }
    //获取返回结果
    private Map<String,Object> parseResponse(SearchHits<SpuInfo> searchHits){
     Map<String,Object> resultMap = new HashMap<>();
     //总记录数
     long totalHits = searchHits.getTotalHits();
    
     //商品列表
     List<SearchHit<SpuInfo>> searchHits1 = searchHits.getSearchHits();
     //这里得到的每个结果是一个SearchHit对象 我们需要一个SpuInfo
     List<SpuInfo> spuInfoList = searchHits1.stream().map(hit -> {
            SpuInfo spuInfo = hit.getContent();
            //取高亮结果
            //Map<String, List<String>> highlightFields = hit.getHighlightFields();
            List<String> highlightField = hit.getHighlightField("spuName");
            if (highlightField.size() > 0) {
                //把高亮结果设置回去
                spuInfo.setSpuName(highlightField.get(0));
            }
            return spuInfo;
        }).collect(Collectors.toList());
      
    resultMap.put("rows",spuInfoList);
    //聚合结果
    Aggregations aggregations = searchHits.getAggregations();
    //品牌的聚合结果
    ParsedStringTerms brandGroup = aggregations.get("brandGroup");
    //每个buckes都是一项聚合结果
    List<String> brandList = brandGroup.getBuckets().stream().map(term ->
            term.getKeyAsString()).collect(Collectors.toList());
    resultMap.put(" brandList", brandList);
    //分类的聚合结果
    ParsedStringTerms categoryGroup = aggregations.get("categoryGroup");
    //每个buckes都是一项聚合结果
    List<String> categoryList = categoryGroup.getBuckets().stream().map(term ->
                term.getKeyAsString()).collect(Collectors.toList());
    resultMap.put("categoryList", categoryList);
    return resultMap;
    }
}
