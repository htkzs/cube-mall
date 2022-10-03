package com.kkb;

import com.kkb.service.UserService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * @author hubin
 * @title: soa 架构远程调用
 * @description:
 * @date 2020/1/421:27
 */
public class SOAClient {
    public static void main(String [] args) throws MalformedURLException, RemoteException {
        //创建访问wsdl服务地址的url
        URL url = new URL("http://localhost:8888/service/UserService?wsdl");
        //通过QName指明服务的具体消息
        QName sname = new QName("http://service.kkb.com/","UserServiceImplService");
        //创建服务
        Service service = Service.create(url,sname);
        //实现接口
        UserService userService=service.getPort(UserService.class);

        String user = userService.getUser(10L);

        System.out.println("最终调用的结果为:"+user);
    }
}
