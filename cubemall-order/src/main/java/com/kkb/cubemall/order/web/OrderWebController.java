package com.kkb.cubemall.order.web;

import com.kkb.cubemall.order.service.OrderService;
import com.kkb.cubemall.order.service.dto.SubmitOrderDTO;
import com.kkb.cubemall.order.web.vo.OrderConfirmVo;
import com.kkb.cubemall.order.web.vo.OrderResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @ClassName OrderWebController
 * @Description
 * @Author hubin
 * @Date 2021/5/30 15:32
 * @Version V1.0
 **/
@Controller
@Slf4j
public class OrderWebController {


    // 注入订单服务对象
    @Autowired
    private OrderService orderService;

    /**
     * @Description: 去到订单结算页 要进入订单页用户必须处于登录状态
     * @Author: hubin
     * @CreateDate: 2021/5/30 15:32
     * @UpdateUser: hubin
     * @UpdateDate: 2021/5/30 15:32
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @RequestMapping("/order/confirm")
    public String confirmOrder(Model model){

        // 查询计算页相关数据
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("confirmVo",confirmVo);

        return "order";
    }

    /**
     * @Description: 下单功能实现
     * @Author: hubin
     * @CreateDate: 2021/6/7 17:23
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 17:23
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @RequestMapping("/order/createOrder")
    public String createOrder(SubmitOrderDTO orderDTO, Model model, RedirectAttributes redirectAttributes){

        // 调用服务层方法，实现下单操作
        OrderResultVo resultVo = orderService.submitOrder(orderDTO);
        // 判断下单是否成功
        if(resultVo.getCode() == 0){
            // 下单成功
            model.addAttribute("submitOrder",resultVo);

            return "pay";
        }

        String msg = "下单失败";

        // 下单失败
        switch (resultVo.getCode()){
            case 1: msg += "令牌信息过期"; break;
            case 2: msg += "订单数据发生变化，请确认后再提交"; break;
            case 3: msg += "库存锁定失败"; break;
        }

        redirectAttributes.addFlashAttribute("msg",msg);
        return "redirect:http://localhost:8084/order/confirm";

    }


}

