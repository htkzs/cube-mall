package com.kkb.cubemall.product.service.impl;

import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;
import com.kkb.cubemall.product.entity.SkuImagesEntity;
import com.kkb.cubemall.product.entity.SpuInfoDescEntity;
import com.kkb.cubemall.product.entity.SpuInfoEntity;
import com.kkb.cubemall.product.service.*;
import com.kkb.cubemall.product.vo.SkuItemSaleAttrVo;
import com.kkb.cubemall.product.vo.SkuItemVo;
import com.kkb.cubemall.product.vo.SpuAttrGroupVo;
import com.kkb.cubemall.product.vo.SpuSaveVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kkb.cubemall.product.dao.SkuInfoDao;
import com.kkb.cubemall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 5.1 保存sku的基本信息:tb_sku_info
     * @param skuInfoEntity
     */
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    /**
     * sku的条件查询
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        //是否携带 key
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w->{
                w.eq("id", key).or().like("sku_name", key);
            });
        }
        //是否携带key
        String categoryId = (String) params.get("categoryId");
        if (!StringUtils.isEmpty(categoryId) && !"0".equalsIgnoreCase(categoryId)){
            wrapper.eq("category_id", categoryId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }

        //是否携带了 价格区间
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && !"0".equalsIgnoreCase(max)) {
            wrapper.le("price", max);
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    /**
     * 查询商品详情页的信息
     * @param skuId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public SkuItemVo skuItem(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1、sku 基本信息
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        skuItemVo.setInfo(skuInfoEntity);
        //2、sku 图片信息（多个图片）
        List<SkuImagesEntity> skuImagesEntityList = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        skuItemVo.setImages(skuImagesEntityList);
        //3、spu 的销售属性
        Long spuId = skuInfoEntity.getSpuId();
        List<SkuItemSaleAttrVo> attrSales = skuSaleAttrValueService.getSaleAttrs(spuId);
        skuItemVo.setAttrSales(attrSales);
        //4、spu 的描述信息
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getOne(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", spuId));
        //5、sku 分组规格参数属性值
        skuItemVo.setDesc(spuInfoDescEntity);
        Long categoryId = skuInfoEntity.getCategoryId();

        List<SpuAttrGroupVo> attrGroupVos = attrGroupService.getGroupAttr(spuId,categoryId);
        skuItemVo.setAttrGroups(attrGroupVos);
        return skuItemVo;
    }

    //使用异步编排改造
    public SkuItemVo skuItemByThread(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1、sku 基本信息
        //开启异步编排 包含返回值使用 supplyAsync
        CompletableFuture<SkuInfoEntity> InfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        },threadPoolExecutor);

        //2、sku 图片信息（多个图片）

        CompletableFuture<Void> imgFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntityList = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            if(skuImagesEntityList.size()>0){
                skuItemVo.setImages(skuImagesEntityList);
            }
        },threadPoolExecutor);


        //3、spu 的销售属性需要使用 spuId依赖于第一步的查询 接收第一步传递的参数，并且不需要返回值

        CompletableFuture<Void> skuSaleAttrValueFuture = InfoFuture.thenAcceptAsync((res) -> {
            Long spuId = res.getSpuId();
            List<SkuItemSaleAttrVo> attrSales = skuSaleAttrValueService.getSaleAttrs(spuId);
            if(attrSales.size()>0){
                skuItemVo.setAttrSales(attrSales);
            }

        },threadPoolExecutor);


        //4、spu 的描述信息 需要依赖于第一步的查询结果 无需返回值
        CompletableFuture<Void> descFuture = InfoFuture.thenAcceptAsync((res) -> {
            Long spuId = res.getSpuId();
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getOne(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", spuId));
            if(spuInfoDescEntity != null){
                skuItemVo.setDesc(spuInfoDescEntity);
            }
        },threadPoolExecutor);

        //5、sku 分组规格参数属性值
        CompletableFuture<Void> attrGroupFuture = InfoFuture.thenAcceptAsync((res) -> {
            Long spuId = res.getSpuId();
            Long categoryId = res.getCategoryId();
            List<SpuAttrGroupVo> attrGroupVos = attrGroupService.getGroupAttr(spuId, categoryId);
            if(attrGroupVos.size()>0){
                skuItemVo.setAttrGroups(attrGroupVos);
            }

        },threadPoolExecutor);
        //主线程需要等待所有的任务都完成后才能返回结果
        CompletableFuture.allOf(InfoFuture,imgFuture,skuSaleAttrValueFuture,descFuture,attrGroupFuture).get();


        return skuItemVo;


    }

}