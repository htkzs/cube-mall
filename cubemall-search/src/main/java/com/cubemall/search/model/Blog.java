package com.cubemall.search.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Author: sublun
 * @Date: 2021/4/25 18:41
 */
@Data
@Document(indexName = "blog_1", shards = 5, replicas = 1)
public class Blog {
    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", store = true)
    private String title;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", store = true)
    private String content;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", store = true)
    private String comment;
    @Field(type = FieldType.Keyword, store = true)
    private String mobile;

}
