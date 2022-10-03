package com.kkb.cubemall.sdes;

import com.cubemall.search.CubemallSearchApplication;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author: sublun
 * @Date: 2021/4/25 18:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CubemallSearchApplication.class)
public class RestClientTest {
    //配置文件已经帮我们自动创建一个客户端
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testRestClient() throws IOException {
        restHighLevelClient.indices().create(new CreateIndexRequest("test"), RequestOptions.DEFAULT);
    }

    //原生ES客户端 聚合查询的使用 注意聚合是在查询的基础上进行
    public void aggregationQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest()
                .indices("blog")
                .source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery())
                        .aggregation(new ValueCountAggregationBuilder("doc_count").field("mobile"))
                        //风组查询 相当于group by
                        .aggregation(new TermsAggregationBuilder("group_count").field("mobile").size(10))
                        //.aggregation(new AvgAggregationBuilder("id_avg").field("id"))
                        //.aggregation(new MaxAggregationBuilder("id_max").field("id"))
                );

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //取原始文档
        SearchHits hits = response.getHits();
        TotalHits totalHits = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();

        System.out.println(response);
    }
}
