package com.kkb.cubemall.common.exception;
//统一异常处理的枚举类型
public enum CubemallEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VAILD_EXCEPTION(10001, "参数格式校验失败"),
    REMOTE_SERVICE_CALL_EXCEPTION(10002,"远程服务调用异常");

    private int code;
    private String msg;

    CubemallEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
