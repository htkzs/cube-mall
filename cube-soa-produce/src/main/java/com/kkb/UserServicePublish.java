package com.kkb;

import com.kkb.service.UserServiceImpl;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * @author hubin
 * @title: UserServicePublish
 * @description: 专注于架构的课程分享
 * @date 2020/1/421:21
 */
public class UserServicePublish {

    public static void main(String[] args) {
        // 发布我们的接口
        Endpoint.publish("http://localhost:8888/service/UserService", new UserServiceImpl());
        System.out.println("服务发布成功");

    }
}
