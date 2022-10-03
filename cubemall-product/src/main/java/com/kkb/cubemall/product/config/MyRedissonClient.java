package com.kkb.cubemall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MyRedissonClient {
    @Bean(destroyMethod = "shutdown")
    RedissonClient redissonClient(){
        Config config = new Config();
        // 可以用"redis://"来启用SSL连接
        config.useSingleServer().setAddress("redis://192.168.2.128:6382");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
