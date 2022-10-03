package com.kkb.cubemall.juc.futuretask;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class FutureTask<I extends Number> {
    //lambda表达式方式开启多线程
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        java.util.concurrent.FutureTask<Integer> integerFutureTask = new java.util.concurrent.FutureTask<>(() -> {
            log.info("Thread01线程执行开始");
            int i = 10 / 5;
            log.info("Thread01线程执行结束");
            return i;
        });
        Thread thread =new Thread(integerFutureTask);
        thread.start();
        Integer integer = integerFutureTask.get();
        log.info("子线程执行结束 返回值为:"+integer);
    }
    //匿名内部类的实现方式
    public void CreateThreadByInnerClass() throws ExecutionException, InterruptedException {
        log.info("主线程执行开始");
        java.util.concurrent.FutureTask<Integer> integerFutureTask = new java.util.concurrent.FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() {
                log.info("Thread01线程执行开始");
                int i = 10 / 5;
                log.info("Thread01线程执行结束");
                return i;
            }
        });
        Thread thread = new Thread(integerFutureTask);
        thread.start();
        //会阻塞主线程的执行，等到子线程获取到结果
        Integer integer = integerFutureTask.get();
        log.info("子线程执行结束 返回值为:"+integer);
    }
    //常规方式
    public void CreateThread() throws ExecutionException, InterruptedException {
        Thread01 thread01 =new Thread01();
        java.util.concurrent.FutureTask<Integer> Task = new java.util.concurrent.FutureTask<Integer>(thread01);
        Thread thread = new Thread(Task);
        thread.start();
        Integer integer = Task.get();
        log.info("子线程执行结束 返回值为:"+integer);
    }


}
