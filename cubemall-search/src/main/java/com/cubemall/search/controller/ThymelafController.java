package com.cubemall.search.controller;

import com.cubemall.search.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.SpringTemplateEngine;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * springboot整合thymeleaf
 */
@Controller
public class ThymelafController {
    //若每次请求需要生成一个静态页面
    @Autowired
    private SpringTemplateEngine springTemplateEngine;
    @GetMapping("/html/hello")
    public String thymeleaf(Model model){
        List<User> userList = new ArrayList<User>();
        User user1 = new User(1,"张三","北京",new Date());
        User user2 = new User(2,"李四","上海",new Date());
        User user3 = new User(3,"王五","广州",new Date());
        User user4 = new User(4,"李磊","北京",new Date());
        User user5 = new User(5,"韩梅梅","北京",new Date());
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);

        Boolean flag = true;

        //5. 向context添加模板使用的变量
        model.addAttribute("hello","hello world!");
        model.addAttribute("html","<h1>Hello world!</h1>");
        model.addAttribute("userList",userList);
        model.addAttribute("flag",flag);
        return "hello";
    }

}
