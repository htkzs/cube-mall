package com.kkb.cubemall.juc.async;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

//异步执行开启的两种方法
//corePoolSize保持和CPU的核心数  maximumPoolSize的数量通常比corePoolSize+1
//keepAliveTime指的是除核心线程以外的其它线程在 设置的时间没有被使用 将被线程池回收
@Slf4j
public class OpenAsync {
    //创建线程池
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            13,
            3,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
            );

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("main主线程start...........");
        log.info("核心线程的数量是:"+Runtime.getRuntime().availableProcessors());
        //OpenAsync.runAsync();
        //OpenAsync.supplyAsync();
        //OpenAsync.thenRun();
        //OpenAsync.thenRunAsync();
        //OpenAsync.thenApply();
        //OpenAsync.thenAccept();
        //OpenAsync.thenCompose();
        //OpenAsync.thenCombine();
        //OpenAsync.thenAcceptBoth();
        //OpenAsync.runAfterBoth();
        //OpenAsync.applyToEither();
        //OpenAsync.exceptionally();
        OpenAsync.whenComplete();
        //OpenAsync.handle();
        log.info("main主线程end...........");
    }
    //runAsync开启异步执行时 没有返回值
    public static void runAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> asyncTask = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
            }
        }, threadPoolExecutor);
        //调用异步任务
        asyncTask.get();
    }

    //supplyAsync()开启异步执行时 有返回值
    public static void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> supplyAsyncFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
                return i;
            }
        }, threadPoolExecutor);
        Integer integer = supplyAsyncFuture.get();
        log.info("异步线程执行的结果是:"+integer);
    }
    //public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,Executor executor)
    //串行关系执行： 利用上一步的执行结果，去进行下一步任务执行，任务执行具有先后顺序， 因此把这种操作叫做串行关系。
    //public CompletionStage<Void> thenRun(Runnable action);
    //thenRun的执行并不关心上一步的执行结果，只和上一步执行具有先后顺序关系 最终结果没有返回值
    public static void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> thenRunFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
                return i;
            }
        }, threadPoolExecutor).thenRun(new Runnable() {
            @Override
            public void run() {
                log.info("thenRun子线程开始执行");
            }
        });
        thenRunFuture.get();
    }
    //thenRunAsync()和thenRun()差不多
    public static void thenRunAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> thenRunAsyncFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
                return i;
            }
        }, threadPoolExecutor).thenRunAsync(new Runnable() {
            @Override
            public void run() {
                log.info("thenRun子线程开始执行");
            }
        });
        thenRunAsyncFuture.get();
    }
    //public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,Executor executor)
    //thenApply 此方法具有返回值，上一步直接的结果当成参数传 递给 thenApply
    //public <U> CompletionStage<U> thenApply(Function<? super T,? extends U> fn);
    //T 就是参数类型，U 就是返回值类型
    public static void thenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> thenApplyFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
                return i;
            }
        }, threadPoolExecutor).thenApply((t) ->{
            log.info("thenApply子线程开始运行，参数是:{}",t);
            long result = t * 5;
            log.info("计算结果是:"+result);
            return result;
        });
        thenApplyFuture.get();
    }
    //public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,Executor executor)
    //public CompletionStage<Void> thenAccept(Consumer<? super T> action);
    //thenAccept 此方法无返回值，上一步直接的结果当成参数传 递给 thenAccept T是参数类型 void无返回值
    public static void thenAccept() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> thenApplyFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
                return i;
            }
        }, threadPoolExecutor).thenAccept((t) ->{
            log.info("thenApply子线程开始运行，参数是:{}",t);
            long result = t * 5;
            log.info("计算结果是:"+result);
        });
        thenApplyFuture.get();
    }
    //public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor);
    //指定两次自定义线程池
    public static void thenAcceptAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> thenApplyFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
                return i;
            }
        }, threadPoolExecutor).thenAcceptAsync((t) ->{
            log.info("thenApply子线程开始运行，参数是:{}",t);
            long result = t * 5;
            log.info("计算结果是:"+result);
        },threadPoolExecutor);
        thenApplyFuture.get();
    }
    //public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);
    //1.有返回值 返回值类型为U
    //2，依赖上一步返回结果 上一步返回结果会被当作参数传递
    //3允许对两个Completionstage流水线进行操作 第一次操作完成后将第一次操作的结果传递给第二个Completionstage
    public static void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> thenComposeFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                log.info("开启异步线程");
                int i = 10 / 5;
                log.info("异步线程执行结束 结果为：{}", i);
                log.info(Thread.currentThread().getName());
                return i;
            }
        }, threadPoolExecutor).thenCompose(new Function<Integer, CompletionStage<Long>>() {
            @Override
            public CompletionStage<Long> apply(Integer integer) {
                CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
                    log.info("thenApply子线程开始运行，参数是:{}", integer);
                    long result = integer * 5;
                    log.info("计算结果是:" + result);
                    return result;
                });
                return completableFuture;
            }
        });
        thenComposeFuture.get();
    }
    //and聚合 当多个任务执行完毕后执行
    //public <U,V> CompletionStage<V> thenCombine (CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn);
    //有返回值 t和u分别为前两个任务的返回值
    public  static void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> thenCombinFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 5;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Integer> thenCombinFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 2;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Integer> integerCompletableFuture = thenCombinFuture1.thenCombine(thenCombinFuture2, (t, u) -> {
            log.info("thenCombinFuture1任务执行的结果是{}", t);
            log.info("thenCombinFuture2任务执行的结果是{}", u);
            return t + u;
        });
        Integer integer = integerCompletableFuture.get();
        log.info("thenCombine执行的结果是{}",integer);

    }
    //1、public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier); //thenAcceptBoth:
    // 当 2 个阶段的 CompletionStage 都执行完毕后，把结果一块交给 thenAcceptBoth 进行执行,没有返回值
    // public <U> CompletionStage<Void> thenAcceptBoth (CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action);
    public  static void thenAcceptBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> thenCombinFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 5;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Integer> thenCombinFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 2;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Void> integerCompletableFuture = thenCombinFuture1.thenAcceptBoth(thenCombinFuture2, (t, u) -> {
            log.info("thenCombinFuture1任务执行的结果是{}", t);
            log.info("thenCombinFuture2任务执行的结果是{}", u);
        });
        integerCompletableFuture.get();
    }

    public  static void runAfterBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> thenCombinFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 5;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Integer> thenCombinFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 2;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Void> integerCompletableFuture = thenCombinFuture1.runAfterBoth(thenCombinFuture2, () -> {
            log.info("runAfterBoth任务正在执行.........");
        });
        integerCompletableFuture.get();
    }
    //OR聚合
    //
    //1.针对于两阶段CompletionStage，将计算最快的那个CompletionStage的结果作为下一步处理的消费
    //2.有返回值
    public  static void applyToEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> thenCombinFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 5;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Integer> thenCombinFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 2;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        });
        CompletableFuture<Object> applyToEitherFuture = thenCombinFuture1.applyToEither(thenCombinFuture2, (t) -> {
            log.info("执行最快的那个任务执行的结果是{}", t);
            log.info("有个任务在执行 applyToEither正在执行...........");
            return null;
        });
        applyToEitherFuture.get();
    }
    //异常处理
    //public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn);
    //exceptionally 会捕获到异常但不会抛出异常 相当于try()
    public  static void exceptionally() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> exceptionallyFuture = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 0;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        }).exceptionally((e) -> {
            if (e != null) {
                log.info("任务运行发生异常 捕获异常结果为{}", e.getMessage());
            }
            return null;
        });
        exceptionallyFuture.get();
    }
    //public CompletionStage<T> whenComplete (BiConsumer<? super T, ? super Throwable> action);
    //会将捕获的异常进行抛出 相当于catch
    public static void whenComplete() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> whenCompleteFuture = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 0;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        }).whenComplete((t, e) -> {
            if (e != null) {
                log.info("任务运行发生异常 异常结果为{}", e.getMessage());
            }else{
                log.info("仍务执行的结果是:{}", t);
            }

        });
        whenCompleteFuture.get();
    }

    //public <U> CompletionStage<U> handle (BiFunction<? super T, Throwable, ? extends U> fn);
    //相当于finally 对上一步执行结果进行处理 关键在于处理异常任务
    public static void handle() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> handleFuture = CompletableFuture.supplyAsync(() -> {
            log.info("开启异步线程");
            int i = 10 / 0;
            log.info("异步线程执行结束 结果为：{}", i);
            log.info(Thread.currentThread().getName());
            return i;
        }).handle((t, u) -> {
            int result = -1;
            if (u != null) {
                log.info("任务运行发生异常 异常结果为{}", u.getMessage());
            }else{
                log.info("仍务执行的结果是:{}", t);
                result = t*5;
            }
            return result;
        });
        Integer integer = handleFuture.get();
    }
}
