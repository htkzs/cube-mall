package com.kkb.cubemall.order.web.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * spu信息
 * 
 * @author jiaoshou
 * @email seaizon@gmail.com
 * @date 2021-04-13 20:26:25
 */
@Data
public class SpuInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 自增ID
	 */
	private Long id;
	/**
	 * spu名称
	 */
	private String spuName;
	/**
	 * spu描述
	 */
	private String spuDescription;
	/**
	 * 分类ID
	 */
	private Long categoryId;
	/**
	 * 品牌名称
	 */
	private String brandName;
	/**
	 * 权重
	 */
	private BigDecimal weight;
	/**
	 * 发布状态
	 */
	private Integer publishStatus;
	/**
	 * 
	 */
	private Date createTime;
	/**
	 * 
	 */
	private Date updateTime;

}
