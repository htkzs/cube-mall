package com.kkb.cubemall.thymeleaf;

import com.cubemall.search.CubemallSearchApplication;
import com.cubemall.search.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = CubemallSearchApplication.class)
@RunWith(SpringRunner.class)
public class ThymeleafEngine {
    @Test
    public void testThymeleaf() throws IOException {
        //1.创建一个基于classpath的路径记载器
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        //2.设置加载器的属性 前缀和后缀
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        //3. 创建一个模板引擎
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver );

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
        //4.创建一个context对象 相当于一个map 向模板传递数据需要使用
        Context context = new Context();
        //5. 向context添加模板使用的变量
        context.setVariable("hello","hello world!");
        context.setVariable("html","<h1>Hello world!</h1>");
        context.setVariable("userList",userList);
        context.setVariable("flag",flag);

        //6.渲染模板，模板所在的位置，使用的context对象，静态文件生成的路径
        FileWriter writer = new FileWriter("D:/FileReporsitory/hello.html");
        templateEngine.process("hello",context,writer);
    }
}
