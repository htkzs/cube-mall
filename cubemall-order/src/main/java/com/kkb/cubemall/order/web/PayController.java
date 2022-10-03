package com.kkb.cubemall.order.web;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.kkb.cubemall.order.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName PayController
 * @Description
 * @Author hubin
 * @Date 2021/6/8 19:10
 * @Version V1.0
 **/
@Controller
public class PayController {


    // 注入支付服务
    @Autowired
    private PayService payService;

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
    @ResponseBody
    @GetMapping(value = "/alipay/order/pay",produces = "text/html")
    public String alipayOrder(@RequestParam("orderId") String orderId){
        String res = payService.alipayOrder(orderId);
        return res;
    }


    /**
     * @Description: 支付完成后，重定向到支付成功页面
     * @Author: hubin
     * @CreateDate: 2021/6/9 12:07
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/9 12:07
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @RequestMapping("/pay/success")
    public String paySuccess(@RequestParam("total_amount") String total_amount, Model model){

        // 把支付金额放入model域
        model.addAttribute("payPrice",total_amount);

        // 返回到支付成功页面
        return "paysuccess";
    }


}

