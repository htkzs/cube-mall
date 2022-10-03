package com.kkb.cubemall.canal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCanalClient//配置开启Canal客户端
public class CubemallCanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CubemallCanalApplication.class, args);
    }

}
