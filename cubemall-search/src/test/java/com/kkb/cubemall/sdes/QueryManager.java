package com.kkb.cubemall.sdes;


import com.cubemall.search.CubemallSearchApplication;
import com.cubemall.search.model.Blog;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CubemallSearchApplication.class)
public class QueryManager {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Test
    public void QueryAllDocument(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery()).build();
        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(searchQuery, Blog.class);
        long totalHits = searchHits.getTotalHits();
        List<SearchHit<Blog>> hits = searchHits.getSearchHits();
        //hits.forEach(System.out::println);
        for (SearchHit<Blog> item : hits) {
            Blog content = item.getContent();
            String title = content.getTitle();
            System.out.println(item);
        }
    }
    @Test
    public void searchByCondition(){
        // matchQuery参数可以是一段话 查询之前会先分词
        //注意只有查询条件才会做高亮处理
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("title", "看电影"))
                .withFilter(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("title", "电影"))
                        .should(QueryBuilders.termQuery("content", "祝福")).filter(QueryBuilders.termQuery("content","音响")))
                .withPageable(PageRequest.of(0, 10)).withHighlightBuilder(new HighlightBuilder()
                        .field("title").field("content").preTags("<em>").postTags("</em>"))
                .addAggregation(new ValueCountAggregationBuilder("value_count").field("mobile"))
                .addAggregation(new TermsAggregationBuilder("mobile_group").field("mobile").size(10))
                .build();
        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(query, Blog.class);
        System.out.println("总命中记录条数:"+searchHits.getTotalHits());
        List<SearchHit<Blog>> hits = searchHits.getSearchHits();
        //可以取文档对象 或者高亮对象
        hits.forEach(item ->{
            //文档对象
            Blog blog = item.getContent();
            //获取多条高亮显示的结果
            Map<String, List<String>> highlightFields = item.getHighlightFields();

            //获取其中一条高亮显示的结果
            List<String> highlightField = item.getHighlightField("title");
            List<String> highlightField1 = item.getHighlightField("content");


            //一般将高亮结果设置回去
            blog.setTitle(highlightField.get(0));
            blog.setContent(highlightField1.get(0));
            System.out.println(blog);

        });
        //过滤和聚合都是在查询基础上进行
        Aggregations aggregations = searchHits.getAggregations();
        ParsedStringTerms aggregation = aggregations.get("mobile_group");
        List<? extends Terms.Bucket> buckets = aggregation.getBuckets();

        aggregation.getBuckets().forEach(e ->{
            System.out.println(e.getKeyAsString());
            e.getDocCount();
        });



    }
}
