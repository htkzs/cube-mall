package com.kkb.cubemall.juc.JUC;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//supplyAsync方式开启异步线程关注返回值
@Slf4j
public class SupplyAsyncDemo {
    public static int i=0;
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            i = 10 / 5;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        supplyAsync.get();
        log.info("默认使用的线程池是"+Thread.currentThread().getName()+"线程执行的结果为：{}",i);
    }
}
