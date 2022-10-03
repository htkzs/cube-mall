package com.kkb.cubemall.product.controller;

import com.kkb.cubemall.product.service.CategoryService;
import com.kkb.cubemall.product.vo.CategoryVo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CategoryService categoryService;
    @GetMapping({"/","/index"})
    public String indexPage(Model model){
        List<CategoryVo> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntities);

        //默认加上前缀和后缀: classpath:/templates/index.html
        return "index";
    }
    //分布式读锁演示
    @ResponseBody
    @GetMapping("/read")
    public String readLock(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        long id = Thread.currentThread().getId();
        try{
            rLock.lock();
            System.out.println("读线程"+id+"加锁成功");
            Thread.sleep(15000);
            stringRedisTemplate.opsForValue().get("readwrite-lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            rLock.unlock();
            System.out.println("读线程"+id+"解锁成功");
        }
        return "read ok";
    }
    //分布式写锁
    @ResponseBody
    @GetMapping("/write")
    public String writeLock(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String uuid = UUID.randomUUID().toString();
        RLock wLock = readWriteLock.writeLock();
        long id = Thread.currentThread().getId();

        try{
            wLock.lock();
            System.out.println("写线程"+id+"加锁成功");
            Thread.sleep(15000);
            stringRedisTemplate.opsForValue().set("readwrite-lock",uuid);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            wLock.unlock();
            System.out.println("写线程"+id+"解锁成功");
        }
        return "write success";
    }
    //信号量 常用于分布式限流
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore parkLock = redissonClient.getSemaphore("park");
        //相当于占用车位 当车位为0时如果还想获取车位就必须等待go()方法调用后再执行
        parkLock.acquire();
        return "ok";
    }
    @ResponseBody
    @GetMapping("/go")
    public String go(){
        RSemaphore parkLock = redissonClient.getSemaphore("park");
        parkLock.release();
        return "释放一个车位";
    }
    //分布式闭锁
    @ResponseBody
    @GetMapping("/door")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("lock-door");
        countDownLatch.trySetCount(5);
        //等待所有的线程都执行完毕后释放 线程阻塞等待
        countDownLatch.await();
        return "关门了.....";
    }
    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Integer id){
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("lock-door");
        //相当于减一操作
        countDownLatch.countDown();
        return id+"桌人走了";
    }
}
