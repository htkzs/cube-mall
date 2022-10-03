package com.kkb.cubemall.product.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;


/**
 * @Author: sublun
 * @Date: 2021/4/25 18:41
 */
@Data
public class Blog {
    @Id
    private Long id;

    private String title;

    private String content;

    private String comment;

    private String mobile;

}
