package com.kkb.cubemall.order.service;

/**
 * @ClassName PayService
 * @Description
 * @Author hubin
 * @Date 2021/6/8 18:33
 * @Version V1.0
 **/
public interface PayService {

    /**
     * @Description: 实现支付宝的支付
     * @Author: hubin
     * @CreateDate: 2021/6/8 18:35
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/8 18:35
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     * @return : 支付宝会给商户返回一个页面，展示支付二维码，实现支付
     */
    public String alipayOrder(String orderId);

}
