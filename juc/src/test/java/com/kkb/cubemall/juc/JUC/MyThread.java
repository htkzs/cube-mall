package com.kkb.cubemall.juc.JUC;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

//继承Thread类方式实现多线程 普通方式
@Slf4j
public class MyThread extends Thread{
    @Override
    public void run() {
        log.info("MyThread线程执行开始");
        int i= 10/5;
        log.info("MyThread线程执行结束");
    }
    public static void main(String[] args) {
        log.info("主线程执行开始'");
        MyThread myThread=new MyThread();
        Thread thread = new Thread(myThread);
        thread.start();
        log.info("主线程执行结束");
    }
}
