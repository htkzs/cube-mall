package com.kkb.cubemall.order.feign;

import com.kkb.cubemall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName ProductRemoteClient
 * @Description
 * @Author hubin
 * @Date 2021/6/7 10:46
 * @Version V1.0
 **/
@FeignClient("cubemall-product")
public interface ProductRemoteClient {


    /**
     * @Description: 根据skuId查询商品spu信息
     * @Author: hubin
     * @CreateDate: 2021/6/7 10:50
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 10:50
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @RequestMapping("/product/spuinfo/spu/{spuId}")
    public R getSpuInfoBySkuId(@PathVariable("spuId") Long skuId);

}
