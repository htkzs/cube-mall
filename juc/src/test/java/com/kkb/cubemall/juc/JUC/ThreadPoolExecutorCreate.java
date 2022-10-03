package com.kkb.cubemall.juc.JUC;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
@Slf4j
public class ThreadPoolExecutorCreate {
    //创建包含一个线程的线程池
    public void CreateSingleThread(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.execute(()->{
                log.info("Executors创建线程池方式创建线程");
                int j = 100 / 3;
                log.info("业务代码执行结果：{}", j);;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            executorService.shutdown();
        }
    }
    //创建固定核心数量线程的线程池
    public void CreateMultiThread(){
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.execute(()->{
                log.info("Executors创建线程池方式创建线程");
                int j = 100 / 3;
                log.info("业务代码执行结果：{}", j);;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            executorService.shutdown();
        }
    }
    //创建一个自动扩容的线程池
    public void CreateCapacityThread(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            executorService.execute(()->{
                log.info("Executors创建线程池方式创建线程");
                int j = 100 / 3;
                log.info("业务代码执行结果：{}", j);;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            executorService.shutdown();
        }
    }
    //建议使用该种方式创建线程池
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6,
            7,
            5000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()); //拒绝策略
    //
    //new ThreadPoolExecutor.AbortPolicy();  新任务直接被拒绝 抛出异常
    //new ThreadPoolExecutor.CallerRunsPolicy(); 新任务来临 直接使用调用者所在的线程执行
    //new ThreadPoolExecutor.DiscardPolicy(); 队列满了 新任务忽略不执行直接抛弃 不会抛出异常
    //new ThreadPoolExecutor.DiscardOldestPolicy();    队列满了 尝试和等待最久的线程竞争，也不会抛出异常：抛弃队列中等待最久的任务，新任务直接添加到队列中
    public static void main(String[] args) {
        try {
            for (int i=0;i<10;i++) {
                threadPoolExecutor.execute(() -> {
                            //业务代码
                            log.info("Executors创建线程池方式创建线程");
                            int j = 100 / 3;
                            log.info("业务代码执行结果：{}", j);;
                        }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            threadPoolExecutor.shutdown();
        };
    }
}




