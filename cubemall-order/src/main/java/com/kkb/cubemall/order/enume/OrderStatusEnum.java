package com.kkb.cubemall.order.enume;

import sun.awt.image.IntegerInterleavedRaster;

/**
 * @ClassName OrderStatusEnum
 * @Description
 * @Author hubin
 * @Date 2021/6/4 17:40
 * @Version V1.0
 **/
public enum OrderStatusEnum {

    ORDER_NEW(0,"待付款"),
    ORDER_PAYED(1,"已付款"),
    ORDER_SEND(2,"已发货"),
    ORDER_REC(3,"已完成"),
    ORDER_CANCEL(4,"已取消"),
    ORDER_SRVING(5,"售后中"),
    ORDER_SVRED(6,"售后完");

    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode(){
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

