package com.kkb.cubemall.juc.JUC;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class AsyncDemo {
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6,
            7,
            5000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //public static CompletableFuture<Void> runAsync(Runnable runnable,Executor executor) 不关注返回值 同步执行
        //指定自定义线程池方式  不指定默认使用的是ForkJoinPool.commonPool
        log.info("主线程开始执行");
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            log.info("开启异步线程");
             int i=10/5;
            log.info("异步线程执行结束 结果为：{}",i);
            log.info(Thread.currentThread().getName());
        },threadPoolExecutor).thenRunAsync(()->{
            log.info("thrnRun子线程开始运行");  //线程的执行只有先后的顺序关系 并不关心上一步的执行结果
        }
        );
        voidCompletableFuture.get();
        log.info("主线程执行结束");
    }
    public void thenRun() throws ExecutionException, InterruptedException {
        //public static CompletableFuture<Void> runAsync(Runnable runnable,Executor executor) 不关注返回值 同步执行
        //指定自定义线程池方式  不指定默认使用的是ForkJoinPool.commonPool
        log.info("主线程开始执行");
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            log.info("开启异步线程");
            int i=10/5;
            log.info("异步线程执行结束 结果为：{}",i);
            log.info(Thread.currentThread().getName());
        },threadPoolExecutor).thenRun(()->{
                    log.info("thrnRun子线程开始运行");  //线程的执行只有先后的顺序关系 并不关心上一步的执行结果
                }
        );
        voidCompletableFuture.get();
        log.info("主线程执行结束");
    }


}
