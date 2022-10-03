package com.kkb.cubemall.seckill.controller;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.seckill.service.SeckillService;
import com.kkb.cubemall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;


    @GetMapping(value = "/getCurrentSeckillSuks")
    @ResponseBody
    public R getCurrentSeckillSkus(){
        List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSuks();
        return R.ok().setData(vos);
    }

    @GetMapping(value = "/uploadSeckillSkuLatest3Days")
    @ResponseBody
    public R uploadSeckillSkus(){
        seckillService.uploadSeckillSkuLateset3Days();
        return R.ok();
    }

    @GetMapping(value = "/sku/seckill/{skuId}")
    @ResponseBody
    public R getSeckillSkuRedisTo(@PathVariable("skuId") Long skuId){
        SeckillSkuRedisTo vos = seckillService.getSeckillSkuRedisTo(skuId);
        return R.ok().setData(vos);
    }
}
