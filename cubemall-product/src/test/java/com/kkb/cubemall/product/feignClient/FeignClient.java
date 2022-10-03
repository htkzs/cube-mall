package com.kkb.cubemall.product.feignClient;

import com.kkb.cubemall.product.CubemallProductApplication;
import com.kkb.cubemall.product.entity.Blog;
import com.kkb.cubemall.product.feign.SearchFeign;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/** 进行远程服务调用
 * @RunWith(SpringRunner.class) 引入Spring容器 防止Autowired失败
 */
@SpringBootTest(classes = CubemallProductApplication.class)
@RunWith(SpringRunner.class)
public class FeignClient {
    @Autowired
    private SearchFeign searchFeign;
    @Test
    public void sayHello(){
        String s = searchFeign.sayHello("毛毛");
        System.out.println(s);
    }
    @Test
    public void sayHello1(){
        String s = searchFeign.sayHello1("张三");
        System.out.println(s);
    }

    public void getMessage(){
        Blog blog = new Blog();
        blog.setId(1l);
        blog.setTitle("hello");
        blog.setContent("world");
        searchFeign.sendMessage(blog);
    }
}
