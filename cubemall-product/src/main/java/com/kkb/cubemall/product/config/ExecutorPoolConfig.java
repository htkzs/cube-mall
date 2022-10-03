package com.kkb.cubemall.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorPoolConfig {
    @Autowired
    private ThreadPoolConfigProperties threadPoolConfigProperties;
    @Bean
    public ThreadPoolExecutor getThreadPool(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                threadPoolConfigProperties.getCorePoolSize(),
                threadPoolConfigProperties.getMaxPoolSize(),
                threadPoolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
                );
        return threadPoolExecutor;
    }
}
