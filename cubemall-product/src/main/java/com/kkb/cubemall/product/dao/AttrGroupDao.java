package com.kkb.cubemall.product.dao;

import com.kkb.cubemall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkb.cubemall.product.vo.GroupAttrParamVo;
import com.kkb.cubemall.product.vo.SpuAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 属性分组表
 * 
 * @author peige
 * @email peige@gmail.com
 * @date 2021-04-19 18:24:09
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuAttrGroupVo> getGroupAttr(GroupAttrParamVo paramVo);
}
