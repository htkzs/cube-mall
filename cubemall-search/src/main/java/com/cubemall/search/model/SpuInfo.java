package com.cubemall.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
@Data
@Document(indexName = "goods_index",shards = 5,replicas = 1)  //设置创建5个分片 每个分片一个副本
public class SpuInfo {
    /**
     * 需要根据id查找到具体的商品
     */
    @Id
    @Field(type = FieldType.Long,store = true)
    private Long id;
    @Field(type = FieldType.Text,analyzer = "ik_max_word",store = true)
    private String spuName;
    @Field(type = FieldType.Text,analyzer = "ik_max_word",store = true)
    private String spuDescription;
    /**
     * categoryId不需要展示 故可以不存储
     */
    @Field(type = FieldType.Keyword)
    private Long categoryId;
    @Field(type = FieldType.Keyword,store = true)
    private String categoryName;
    @Field(type = FieldType.Keyword,store = true)
    private String brandName;

    @Field(type = FieldType.Long)
    private Long brandId;
    /**
     * 搜索时可能需要展示图片
     */
    @Field(type = FieldType.Keyword,store = true,index = false)
    private String brandImage;
    /**
     * 考虑到可能查找最新上架的产品
     */
    @Field(type = FieldType.Date,store = true,format = DateFormat.basic_date_time)
    private Date updateTime;
    /**
     * 记录一张 default的图片的URL
     */
    @Field(type = FieldType.Keyword, store = true, index = false)
    private String imageUrl;
    /**
     * 对于数值类型一般不分词 但可能根据价格查询 所以会创建索引
     */
    @Field(type = FieldType.Double,store = true)
    private Double price;

}
