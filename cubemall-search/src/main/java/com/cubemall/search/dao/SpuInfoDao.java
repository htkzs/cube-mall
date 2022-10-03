package com.cubemall.search.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cubemall.search.entity.SpuInfoEntity;
import com.cubemall.search.model.SpuInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * spu信息
 * 
 * @author peige
 * @email peige@gmail.com
 * @date 2021-04-19 18:24:09
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {
	/**
	 * 增量方法
	 * @param spuId
	 * @return
	 */
	SpuInfo getNewSpuInformation(Long spuId);

	/**
	 * 全量查询
	 * @return
	 */
	List<SpuInfo> getAllSpuInformation();

}
