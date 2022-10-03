package com.kkb.cubemall.sdes;

import com.cubemall.search.CubemallSearchApplication;
import com.cubemall.search.model.Blog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: sublun
 * @Date: 2021/4/25 18:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CubemallSearchApplication.class)
public class RestTemplateTest {
    @Autowired
    private ElasticsearchRestTemplate template;
    //创建索引库
    @Test
    public void createIndex() {
        template.indexOps(IndexCoordinates.of("mytest")).create();
    }
    //创建索引库添加Mapping
    @Test
    public void putMapping() {
        Document mapping = template.indexOps(IndexCoordinates.of("mytest")).createMapping(Blog.class);
        template.indexOps(IndexCoordinates.of("mytest")).putMapping(mapping);
    }
    //创建索引库添加Mapping
    @Test
    public void createIndexWithMapping() {
        //template.indexOps(Blog.class).create();
        Document mapping = template.indexOps(IndexCoordinates.of("mytest")).createMapping(Blog.class);
        //此时会解析Blog中的注解得到索引名称
        template.indexOps(Blog.class).putMapping(mapping);
    }
    //删除索引
    @Test
    public void deleteIndex() {
        template.indexOps(IndexCoordinates.of("hello1")).delete();
    }
}
