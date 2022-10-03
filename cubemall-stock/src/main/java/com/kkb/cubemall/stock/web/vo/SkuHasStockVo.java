package com.kkb.cubemall.stock.web.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName SkuHasStockVo
 * @Description
 * @Author hubin
 * @Date 2021/6/7 16:40
 * @Version V1.0
 **/
@ToString
@Data
public class SkuHasStockVo {

    private Long skuId;
    private Integer num;
    private List<Long> stockId;

}

