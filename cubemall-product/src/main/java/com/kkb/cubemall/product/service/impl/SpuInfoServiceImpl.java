package com.kkb.cubemall.product.service.impl;

import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.entity.*;
import com.kkb.cubemall.product.exception.RemoteServiceCallException;
import com.kkb.cubemall.product.feign.SearchFeign;
import com.kkb.cubemall.product.service.*;
import com.kkb.cubemall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kkb.cubemall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SearchFeign searchFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 对7张数据库表进行保存操作
     * @param spuSaveVo
     */
    @Transactional(rollbackFor = MethodArgumentNotValidException.class)
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1.保存spu基本信息: tb_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());

        this.saveBaseSpuInfo(spuInfoEntity);
        //this.save(spuInfoEntity);

        //2.保存spu的描述信息: tb_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        //第一步插入spuInfoEntity时生成的id
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(spuSaveVo.getDecript());

        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3.保存spu图片集: tb_spu_images
        List<String> images = spuSaveVo.getImages();

        spuImagesService.saveImage(spuInfoEntity.getId(), images);

        //4.保存spu的规格参数: tb_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrId((long) attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            //前端只考虑属性id不传递属性的名称。
            productAttrValueEntity.setAttrName(attrEntity.getName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());

        productAttrValueService.saveProductAttr(collect);

        //5.保存当前spu对应的所有的sku信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size()>0) {
            skus.forEach(item->{
                //获取默认图片
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                //5.1 保存sku的基本信息:tb_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCategoryId(spuInfoEntity.getCategoryId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                skuInfoService.saveSkuInfo(skuInfoEntity);

                //5.2 保存sku的图片信息:tb_sku_images
                Long skuId = skuInfoEntity.getId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    //插入不足9个时导致插入空记录 所以对空的对象进行过滤
                    return StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                skuImagesService.saveBatch(imagesEntities);

                //5.3 保存sku的销售属性: tb_sku_sale_attr_value
                List<Attr> attrs = item.getAttr();
                if (attrs != null && attrs.size() > 0) {
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuId);
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());

                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                }
            });
        }
    }

    /**
     * spu的条件查询
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByConditon(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //是否携带key
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w->{
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        //是否携带分类id,品牌id
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String categoryId = (String) params.get("categoryId");
        if (!StringUtils.isEmpty(categoryId) && !"0".equalsIgnoreCase(categoryId)) {
            wrapper.eq("category_id", categoryId);
        }

        //是否携带status
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("public_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    /**
     * 数据库上架状态的修改和 ES索引库的数据同步  @Transactional默认只回滚运行时异常和error 而对自定义的业务异常不做检查 故需要使用rollbackFor
     * @param spuId
     * @return
     */
    @Override
    @Transactional(rollbackFor = RemoteServiceCallException.class)
    public R putOnSale(Long spuId) throws RemoteServiceCallException {
    // 1.接收controller传递过来的参数，spuId
    // 2.根据spuID找到对应的商品修改商品状态
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setPublishStatus(1);
    //    修改tb_spuinfo表中的publish_status字段为1
        this.baseMapper.updateById(spuInfoEntity);
    // 3.需要把商品添加到索引库中，调用search工程的服务，实现ES索引库的添加。
        //如果添加索引库的操作失败，则需要抛出异常
        try{
            searchFeign.setNewSpuInfo(spuId);
        }catch(Exception e){
            e.printStackTrace();
            throw new RemoteServiceCallException();
        }
    // 4.返回结果
        return R.ok("商品上架成功");
    }

    /**
     * 1.保存spu基本信息: tb_spu_info
     * @param spuInfoEntity
     */
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}