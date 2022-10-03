package com.kkb.cubemall.stock.service.impl;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.stock.entity.StockOrderTaskDetailEntity;
import com.kkb.cubemall.stock.entity.StockOrderTaskEntity;
import com.kkb.cubemall.stock.service.StockOrderTaskDetailService;
import com.kkb.cubemall.stock.service.StockOrderTaskService;
import com.kkb.cubemall.stock.web.vo.OrderItemVo;
import com.kkb.cubemall.stock.web.vo.SkuHasStockVo;
import com.kkb.cubemall.stock.web.vo.StockSkuLockVo;
import com.kkb.cubemall.stock.web.vo.StockSkuVo;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;

import com.kkb.cubemall.stock.dao.StockSkuDao;
import com.kkb.cubemall.stock.entity.StockSkuEntity;
import com.kkb.cubemall.stock.service.StockSkuService;
import org.springframework.util.StringUtils;


@Service("stockSkuService")
public class StockSkuServiceImpl extends ServiceImpl<StockSkuDao, StockSkuEntity> implements StockSkuService {

    // 注入orderTaskService
    @Autowired
    private StockOrderTaskService orderTaskService;


    // 注入dao
    @Autowired
    private StockSkuDao stockSkuDao;

    @Autowired
    private StockOrderTaskDetailService orderTaskDetailService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StockSkuEntity> page = this.page(
                new Query<StockSkuEntity>().getPage(params),
                new QueryWrapper<StockSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @Description: 根据skuID查询数据信息
     * @Author: hubin
     * @CreateDate: 2021/6/1 15:37
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/1 15:37
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @Override
    public R getSkuStock(List<Long> skuIds) {

        // 创建一个集合，封装对象
        // stream
        List<StockSkuVo> stockSkuList =  skuIds.stream().map(skuId -> {
            // 根据skuID查询库存信息
            StockSkuEntity stockSkuEntity = this.baseMapper.selectOne(new QueryWrapper<StockSkuEntity>().eq("sku_id", skuId));
            // 获取库存数量
            Integer stock = stockSkuEntity.getStock();

            // 创建封装数据对象
            StockSkuVo stockSkuVo = new StockSkuVo();
            stockSkuVo.setSkuId(skuId);
            // 是否有库存
            stockSkuVo.setHasStock(stock == null?false:stock>0);
            return stockSkuVo;
        }).collect(Collectors.toList());

        return R.ok().setData(stockSkuList);
    }

    /**
     * @Description: 调用库存服务，锁定库存
     * @Author: hubin
     * @CreateDate: 2021/6/7 16:17
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 16:17
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @Override
    public boolean lockOrderStock(StockSkuLockVo stockSkuLockVo) {


        // 订单字段，保证锁定状态
        boolean skuStockLocked = false;

        // 保存库存订单工作单信息
        StockOrderTaskEntity orderTaskEntity = new StockOrderTaskEntity();
        orderTaskEntity.setOrderSn(stockSkuLockVo.getOrderId());
        orderTaskEntity.setCreateTime(new Date());
        // 保存
        orderTaskService.save(orderTaskEntity);

        // 按照收货地址，找到一个最近的仓库，锁定库存
        // 找到每一个商品属于哪个库存，且库存是否充足，如果条件满足，就锁定库存
        // 获取订单商品明细
        List<OrderItemVo> lockList = stockSkuLockVo.getLockList();

        List<SkuHasStockVo> hasStockVoList = lockList.stream().map(item->{
            // 创建对象保存待锁定的库存信息
            SkuHasStockVo hasStockVo = new SkuHasStockVo();
            hasStockVo.setSkuId(item.getSkuId());
            hasStockVo.setNum(item.getCount());

            //查询这个商品在哪个仓库有库存
            //总库存减去购买商品数量，如果大于>0 ,那么表示可以锁定库存
            List<Long> stockIds = stockSkuDao.selectHasStockSListStockIds(item.getSkuId());
            hasStockVo.setStockId(stockIds);
            return hasStockVo;

        }).collect(Collectors.toList());


        // 锁定库存核心
        for (SkuHasStockVo hasStockVo : hasStockVoList) {

            // 获取仓库数据
            List<Long> stockIds = hasStockVo.getStockId();
            // 判断仓库是否存在
            if(!StringUtils.isEmpty(stockIds)){

                // 锁定每一个商品的库存
                for (Long stockId : stockIds) {

                    // 开始锁定，锁定成功返回1，否则返回0
                    Long count = stockSkuDao.lockSkuStock(hasStockVo.getSkuId(),stockId,hasStockVo.getNum());

                    // 判断锁定库存是否成功
                    if(count == 1){

                        skuStockLocked = true;

                        // 锁定库存成功，保存工作单详细信息
                        StockOrderTaskDetailEntity orderTaskDetailEntity = new StockOrderTaskDetailEntity();
                        orderTaskDetailEntity.setSkuId(hasStockVo.getSkuId());
                        orderTaskDetailEntity.setSkuName("");
                        orderTaskDetailEntity.setSkuNum(hasStockVo.getNum());
                        orderTaskDetailEntity.setTaskId(orderTaskEntity.getId());

                        // 保存实体对象
                        orderTaskDetailService.save(orderTaskDetailEntity);

                    }

                }

            }

        }

        return skuStockLocked;
    }

}