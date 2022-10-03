package com.kkb.cubemall.juc.JUC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyThread1 implements Runnable{
    @Override
    public void run() {
        log.info("MyThread线程执行开始");
        int i= 10/5;
        log.info("MyThread线程执行结束");
    }
    //lambda表达式实现多线程
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            log.info("MyThread线程执行开始");
            int i = 10 / 5;
            log.info("MyThread线程执行结束");
        });
        log.info(thread.getId()+thread.getName());
        thread.start();
    }
    //普通方式实现多线程
    public void createthread(){
        MyThread1 myThread1 =new MyThread1();
        Thread thread=new Thread(myThread1);
        thread.start();
    }
    //匿名内部类实现多线程
    public void Createthread(){
        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("MyThread线程执行开始");
                int i= 10/5;
                log.info("MyThread线程执行结束");
            }
        });
        thread.start();
    }

}
