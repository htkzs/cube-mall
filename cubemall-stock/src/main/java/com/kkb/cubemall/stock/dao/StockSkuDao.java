package com.kkb.cubemall.stock.dao;

import com.kkb.cubemall.stock.entity.StockSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author ithubin
 * @email ithubin@gmail.com
 * @date 2021-06-01 15:14:09
 */
@Mapper
public interface StockSkuDao extends BaseMapper<StockSkuEntity> {

    List<Long> selectHasStockSListStockIds(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId") Long skuId, @Param("stockId") Long stockId, @Param("num") Integer num);
}
