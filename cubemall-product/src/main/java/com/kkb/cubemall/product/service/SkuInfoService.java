package com.kkb.cubemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.product.entity.SkuInfoEntity;
import com.kkb.cubemall.product.vo.SkuItemVo;
import com.kkb.cubemall.product.vo.SpuSaveVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku??Ϣ
 *
 * @author peige
 * @email peige@gmail.com
 * @date 2021-04-19 18:24:09
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * @Description: 商品详情页数据加载接口
     * @Author: hubin
     * @CreateDate: 2021/5/15 16:29
     * @UpdateUser: hubin
     * @UpdateDate: 2021/5/15 16:29
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    public SkuItemVo skuItem(Long skuId) throws ExecutionException, InterruptedException;
}

