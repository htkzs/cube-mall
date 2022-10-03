package com.cubemall.search.controller;

import com.cubemall.search.service.SpuInfoService;
import com.kkb.cubemall.common.utils.R;
import com.sun.el.lang.ELArithmetic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spuinfo")
public class SpuInfoController {
    /**
     * 定义一个线程标记 标记该线程师傅执行
     */
    private volatile boolean executeFlag = false;
    @Autowired
    private SpuInfoService spuInfoService;
    @RequestMapping("/putonsale/{spuId}")
    public R setNewSpuInfo(@PathVariable("spuId") Long spuId){
        R r = spuInfoService.getSpuInfo(spuId);
        return r;

    }

    /**
     * 该过程是一个耗时操作为防止重复执行 通过DCL实现线程同步
     * @return
     */
    @RequestMapping("/putonsaleall")
    public R syncSpuInfo(){
        if(!executeFlag){
            synchronized (this){
                if(!executeFlag){
                    executeFlag = true;
                    R r = spuInfoService.getAllSpuInfo();
                    return r;
                }

            }

        }
        return R.ok("正在执行批量导入操作，请勿重复操作");
    }
}
