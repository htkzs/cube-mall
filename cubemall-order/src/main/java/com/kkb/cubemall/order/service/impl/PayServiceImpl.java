package com.kkb.cubemall.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.kkb.cubemall.order.config.AlipayTemplate;
import com.kkb.cubemall.order.service.OrderService;
import com.kkb.cubemall.order.service.PayService;
import com.kkb.cubemall.order.web.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName PayServiceImpl
 * @Description
 * @Author hubin
 * @Date 2021/6/8 18:36
 * @Version V1.0
 **/
@Service
public class PayServiceImpl implements PayService {


    // 注入订单服务对象
    @Autowired
    private OrderService orderService;

    // 注入阿里支付配置对象
    @Autowired
    private AlipayTemplate alipayTemplate;


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
    @Override
    public String alipayOrder(String orderId) {

        // 根据订单id查询支付需要的数据
        // 支付金额
        // 订单描述
        // 商户订单号
        // 订单名称
        PayVo payVo = orderService.getOrderById(orderId);

        // 开始实现支付操作
        // 1、根据支付配置生成支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(alipayTemplate.getGateway_url(),
                alipayTemplate.getApp_id(),
                alipayTemplate.getMerchant_private_key(),
                alipayTemplate.getAlipay_format(),
                alipayTemplate.getCharset(),
                alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getSign_type()
                );

        //2、创建阿里支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 重定向地址
        alipayRequest.setReturnUrl(alipayTemplate.getReturn_url());
        // 回调地址
        alipayRequest.setNotifyUrl(alipayTemplate.getNotify_url());


        // 获取待构造的数据
        String out_trade_no = payVo.getOut_trade_no();
        String total_amount = payVo.getTotal_amount();
        String body = payVo.getBody();
        String subject = payVo.getSubject();

        String jsonData = "{\"out_trade_no\": \""+out_trade_no+"\"," +
                "\"total_amount\":\""+total_amount+"\"," +
                " \"body\":\""+body+"\"," +
                " \"subject\":\""+subject+"\"," +
                " \"product_code\": \"FAST_INSTANT_TRADE_PAY\" }";


        alipayRequest.setBizContent(jsonData);
        String result = null;

        try {
            // 发送请求
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return result;

    }
}

