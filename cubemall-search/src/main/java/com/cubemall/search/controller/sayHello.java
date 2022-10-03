package com.cubemall.search.controller;

import com.cubemall.search.model.Blog;
import org.springframework.web.bind.annotation.*;

/**
 * 远程服务的提供者
 */
@RestController
public class sayHello {
    /**
     * ant风格传递参数
     * @param name
     * @return
     */
    @GetMapping("/hello/{name}")
    public String sayhello(@PathVariable String name){
        return "Hello"+name;
    }

    /**
     * 通过地址栏传递参数
     * @param name @RequestParam可省略
     * @return
     */
    @GetMapping("/hello")
    public String sayhello1(String name){
        return "Hello"+name;
    }

    /**
     * 通过post传递对象的方式 远程方法调用
     */
    @PostMapping("/send")
    public void sendMessage(@RequestBody Blog blog){
        blog.setComment("OK");
        blog.setMobile("1111");
        System.out.println(blog);
    }

    @PostMapping("/send/message")
    public void sendMessage(@RequestBody Blog blog,String name){
        blog.setComment("OK"+name);
        blog.setMobile("1111");
        System.out.println(blog);
    }
}
