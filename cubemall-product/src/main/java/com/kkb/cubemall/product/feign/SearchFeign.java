package com.kkb.cubemall.product.feign;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.entity.Blog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 通常都是一个接口对应一个微服务 @PathVariable("name")必须写 这里相当于反向操作
 */
@FeignClient("cubemall-product")
public interface SearchFeign {
    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable("name") String name);

    @GetMapping("/hello")
    public String sayHello1(@RequestParam("name") String name);

    @PostMapping("/send")
    public void sendMessage(@RequestBody Blog blog);

    @PostMapping("/send")
    public void sendMessage(@RequestBody Blog blog,@RequestParam String name);

    /**
     * 远程调用ES增量同步
     * @param spuId
     * @return
     */
    @RequestMapping("putonsale/{spuId}")
    public R setNewSpuInfo(@PathVariable("spuId") Long spuId);

}
