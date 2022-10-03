package com.kkb.cubemall.juc.JUC;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class SupplyAsync {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> objectCompletableFuture = CompletableFuture.supplyAsync(() -> {
                log.info("开启异步线程");
                int i=10/5;
                log.info("异步线程执行结束 结果为：{}",i);
                log.info(Thread.currentThread().getName());
                return i;
        }).thenApply((i)->{
            log.info("thenApply线程开始执行");
            return i*5;
        });

        Integer integer = objectCompletableFuture.get();
        log.info("最总计算结果为：{}",integer);

    }

}
