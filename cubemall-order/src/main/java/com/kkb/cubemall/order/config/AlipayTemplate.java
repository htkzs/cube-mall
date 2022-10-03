package com.kkb.cubemall.order.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName AlipayTemplate
 * @Description
 * @Author hubin
 * @Date 2021/6/8 17:15
 * @Version V1.0
 **/
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayTemplate {

    // 应用ID
    private String app_id;
    // 商户私钥
    private String merchant_private_key;
    // 阿里公钥
    private String alipay_public_key;
    // 回调地址，必须是公网地址，外部必须可以访问的地址
    private String notify_url;

    // 支付成功，跳转到本地项目的地址
    private String return_url;
    // 指定签名的方式
    private String sign_type;
    // 支付的编码格式
    private String charset;
    // 网关地址
    private String gateway_url;
    // 格式
    private String alipay_format;
    // 超时时间
    private String timeout;


}

