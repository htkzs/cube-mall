package com.kkb.cubemall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 三级分类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category3Vo implements Serializable {

    private String id;
    private String name;
    private String category2Id;
}
