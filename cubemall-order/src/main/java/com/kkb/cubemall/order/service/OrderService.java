package com.kkb.cubemall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.order.entity.OrderEntity;
import com.kkb.cubemall.order.service.dto.SubmitOrderDTO;
import com.kkb.cubemall.order.web.vo.OrderConfirmVo;
import com.kkb.cubemall.order.web.vo.OrderResultVo;
import com.kkb.cubemall.order.web.vo.PayVo;

import java.util.Map;

/**
 * 订单
 *
 * @author ithubin
 * @email ithubin@gmail.com
 * @date 2021-05-28 18:50:54
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * @Description: 查询订单结算页相关数据
     *
     * 1、地址收货信息
     * 2、购物清单
     * @Author: hubin
     * @CreateDate: 2021/5/31 15:02
     * @UpdateUser: hubin
     * @UpdateDate: 2021/5/31 15:02
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    OrderConfirmVo confirmOrder();

    /**
     * @Description: 提交订单的方法
     * @Author: hubin
     * @CreateDate: 2021/6/4 15:53
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/4 15:53
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    OrderResultVo submitOrder(SubmitOrderDTO submitOrderDTO);

    /**
     * @Description: 根据订单id查询订单信息
     * @Author: hubin
     * @CreateDate: 2021/6/8 18:40
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/8 18:40
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    PayVo getOrderById(String orderId);
}

