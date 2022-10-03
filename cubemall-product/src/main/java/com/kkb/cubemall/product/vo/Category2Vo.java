package com.kkb.cubemall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category2Vo implements Serializable {

    private String id;
    private String name;
    private String category1Id; // 一级父分类id
    //关联的三级分类
    private List<Category3Vo> Category3VoList;
}
