package com.kkb.cubemall.order.web.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName PayVo
 * @Description
 * @Author hubin
 * @Date 2021/6/8 18:37
 * @Version V1.0
 **/
@ToString
@Data
public class PayVo {

    // 商户订单号
    private String out_trade_no;
    // 订单名称
    private String subject;
    // 付款金额
    private String total_amount;
    // 订单描述
    private String body;

}

