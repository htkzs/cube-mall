package com.kkb.cubemall.order.web.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName StockSkuLockVo
 * @Description
 * @Author hubin
 * @Date 2021/6/7 16:06
 * @Version V1.0
 **/
@ToString
@Data
public class StockSkuLockVo {

    private String orderId;

    /*锁定商品库存信息*/
    private List<OrderItemVo> lockList;

}

