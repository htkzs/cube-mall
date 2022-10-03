package com.kkb.cubemall.juc.futuretask;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
//FutureTask创建多线程的方式可以有返回值
@Slf4j
public class Thread01 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        log.info("Thread01线程执行开始");
        int i=10/5;
        log.info("Thread01线程执行结束");
        return i;
    }
}
