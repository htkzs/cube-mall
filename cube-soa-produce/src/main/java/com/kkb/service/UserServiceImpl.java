package com.kkb.service;


import javax.jws.WebService;

/**
 * @ClassName UserServiceImpl
 * @Description
 * @Author hubin
 * @Date 2021/6/10 14:05
 * @Version V1.0
 **/
@WebService(endpointInterface = "com.kkb.service.UserService")
public class UserServiceImpl implements UserService {
    public String getUser(Long userId) {
        return "SOA架构的SOAP协议远程调用:"+userId;
    }
}

