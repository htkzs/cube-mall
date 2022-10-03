package com.kkb.cubemall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableFeignClients(basePackages = {"com.kkb.cubemall.product.feign"})
public class CubemallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(CubemallProductApplication.class, args);
    }

}
