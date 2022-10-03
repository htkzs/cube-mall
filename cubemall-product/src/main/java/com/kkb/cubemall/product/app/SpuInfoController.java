package com.kkb.cubemall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.exception.RemoteServiceCallException;
import com.kkb.cubemall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kkb.cubemall.product.entity.SpuInfoEntity;
import com.kkb.cubemall.product.service.SpuInfoService;



/**
 * spu信息
 *
 * @author peige
 * @email peige@gmail.com
 * @date 2021-04-22 11:03:03
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表 URL localhost:8888/api/product/spuinfo/list?t=1622020770679&page=1&limit=10&key=mini&brandId=14026
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByConditon(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo spuSaveVo){
		spuInfoService.saveSpuInfo(spuSaveVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 商品上架 和ES增量同步 抛出的异常将由全局的异常处理器捕获
     */
    @PostMapping("/{spuid}/up")
    public R puOnSale(Long spuId) throws Exception {
        R r = spuInfoService.putOnSale(spuId);
        return r;
    }
}
