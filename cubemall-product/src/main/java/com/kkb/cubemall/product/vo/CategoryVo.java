package com.kkb.cubemall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 一级分类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryVo implements Serializable {

    private String id;
    private String name;
    // 关联的二级分类集合
    private List<Category2Vo> Category2VoList;
}
