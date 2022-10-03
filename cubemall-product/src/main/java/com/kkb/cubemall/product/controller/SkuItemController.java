package com.kkb.cubemall.product.controller;

import com.kkb.cubemall.product.service.SkuInfoService;
import com.kkb.cubemall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * 返回商品详情页的数据视图
 */
@Controller
public class SkuItemController {
    @Autowired
    private SkuInfoService skuInfoService;
    @GetMapping("/{spuId}.html")
    public String skuItem(@PathVariable Long spuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = skuInfoService.skuItem(spuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }
}
