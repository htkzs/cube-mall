package com.kkb.cubemall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.order.entity.OrderItemEntity;
import com.kkb.cubemall.order.entity.UserEntity;
import com.kkb.cubemall.order.enume.OrderStatusEnum;
import com.kkb.cubemall.order.feign.AuthRemoteClient;
import com.kkb.cubemall.order.feign.CartRemoteClient;
import com.kkb.cubemall.order.feign.ProductRemoteClient;
import com.kkb.cubemall.order.feign.StockRemoteClient;
import com.kkb.cubemall.order.interceptor.OrderInterceptor;
import com.kkb.cubemall.order.service.OrderItemService;
import com.kkb.cubemall.order.service.dto.OrderDTO;
import com.kkb.cubemall.order.service.dto.SubmitOrderDTO;
import com.kkb.cubemall.order.utils.Constants;
import com.kkb.cubemall.order.web.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;

import com.kkb.cubemall.order.dao.OrderDao;
import com.kkb.cubemall.order.entity.OrderEntity;
import com.kkb.cubemall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    // 定义全局threadLocal
    private ThreadLocal<SubmitOrderDTO> submitOrderDTOThreadLocal = new ThreadLocal<>();


    // 注入线程池对象
    @Autowired
    private ThreadPoolExecutor poolExecutor;

    // 注入远程调用地址接口
    @Autowired
    private AuthRemoteClient authRemoteClient;

    // 注入购物车远程调用接口
    @Autowired
    private CartRemoteClient cartRemoteClient;

    // 注入库存服务
    @Autowired
    private StockRemoteClient stockRemoteClient;

    // 注入商品服务
    @Autowired
    private ProductRemoteClient productRemoteClient;


    // 注入redis模板服务
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    // 注入订单明细服务对象
    @Autowired
    private OrderItemService orderItemService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @Description: 查询订单结算页相关数据
     *
     * 1、地址收货信息
     * 2、购物清单
     * @Author: hubin
     * @CreateDate: 2021/5/31 15:02
     * @UpdateUser: hubin
     * @UpdateDate: 2021/5/31 15:02
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @Override
    public OrderConfirmVo confirmOrder() {

        // 创建订单结算页视图对象
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        // 获取请求上下文信息
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 1、 查询用户地址信息
        // 获取当前用户信息
        UserEntity userInfo = OrderInterceptor.dtoThreadLocal.get();
        //拦截器放行 这里用户一定是登录状态
        // 根据用户id查询用户信息
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {

            // 异步请求之前，共享请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);

            // 远程调用用户授权认证，用户中心服务，查询用户数据
            List<UserReceiveAddressVo> userReceiveAddressVos = authRemoteClient.addressList(userInfo.getId());
            // 把用户地址信息添加到订单结算实体对象
            confirmVo.setUserReceiveAddressList(userReceiveAddressVos);
        }, poolExecutor);

        // 2、查询购物车清单数据
        CompletableFuture<Void> cartItemsFuture = CompletableFuture.runAsync(() -> {

            // 异步请求之前，共享请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);

            // 远程调用购物车服务方法，查询购物车清单
            List<CartItemVo> cartItems = cartRemoteClient.getCartItems();
            confirmVo.setItems(cartItems);
        }, poolExecutor).thenRunAsync(()->{
            // 获取购物车清单数据
            List<CartItemVo> items = confirmVo.getItems();

            // 获取所有的skuIds
            List<Long> skuIds = items.stream().map(itemVo -> itemVo.getSkuId()).collect(Collectors.toList());
            // 根据商品skuID查询每一个商品的库存信息
            // 调用库存服务
            R skuStock = stockRemoteClient.getSkuStock(skuIds);

            // 获取是否具有库存数据
            List<StockSkuVo> skuStockDataList = skuStock.getData("data", new TypeReference<List<StockSkuVo>>() {
            });

            // 把是否具有库存的数据转换为map结构数据
            if(skuStockDataList!=null && skuStockDataList.size()>0){
                Map<Long, Boolean> hasStock = skuStockDataList.stream().collect(Collectors.toMap(StockSkuVo::getSkuId, StockSkuVo::getHasStock));
                // 添加对象
                confirmVo.setStocks(hasStock);
            }

        },poolExecutor);


        // 3、 获取用户优惠券信息
        Integer integration = userInfo.getIntegration();
        confirmVo.setIntegration(integration);

        //4、获取商品库存信息，判断商品是否具有库存

        // 防重令牌
        // 由于网络延迟（重试），订单提交按钮可能多次提交
        // 防重令牌，提交订单时携带此令牌
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(Constants.ORDER_TOKEN_PREFIX+userInfo.getId(),token,30, TimeUnit.MINUTES);
        // 放入令牌对象属性
        confirmVo.setOrderToken(token);

        //同步调用
        try {
            CompletableFuture.allOf(addressFuture,cartItemsFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return confirmVo;
    }

    /**
     * @Description: 提交订单的方法
     * @Author: hubin
     * @CreateDate: 2021/6/4 15:53
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/4 15:53
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @Override
    public OrderResultVo submitOrder(SubmitOrderDTO submitOrderDTO) {


        // 放入threadLocal对象
        submitOrderDTOThreadLocal.set(submitOrderDTO);


        // 构建一个返回值对象
        OrderResultVo resultVo = new OrderResultVo();

        // 获取登录用户信息
        UserEntity userInfo = OrderInterceptor.dtoThreadLocal.get();

        // 根据参数令牌，验证令牌信息，防止订单重复提交
        // 获取，验证令牌整个操作必须是一个原子性的操作
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return 1 else return 0 end";
        // 获取传递的令牌
        String orderToken = submitOrderDTO.getOrderToken();
        // 执行lua脚本，返回执行结果：1,0
        Long res = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(Constants.ORDER_TOKEN_PREFIX + userInfo.getId()),
                orderToken);
        // 判断验证令牌是否成功
        if(res == 0L){
            // 令牌验证失败
            // 1表示验证令牌失败
            resultVo.setCode(1);
            return resultVo;
        }

        // 抽取一个方法，构造订单数据
        OrderDTO orderDTO = this.createOrderDTO();
        //  保存订单数据
        this.saveOrder(orderDTO);

        // 库存
        // 定义锁定库存对象
        StockSkuLockVo stockSkuLockVo = new StockSkuLockVo();
        // 设置属性值
        stockSkuLockVo.setOrderId(orderDTO.getOrderEntity().getOrderSn());

        // 设定锁定商品数据信息
        List<OrderItemVo> itemVos = orderDTO.getOrderItemList().stream().map(item -> {
            OrderItemVo itemVo = new OrderItemVo();
            itemVo.setSkuId(item.getSkuId());
            itemVo.setCount(item.getSkuQuantity());
            itemVo.setTitle(item.getSkuName());
            return itemVo;

        }).collect(Collectors.toList());

        // 把需要锁定库存商品信息放入锁定对象
        stockSkuLockVo.setLockList(itemVos);

        // 开始锁定库存
        R r = stockRemoteClient.lockOrderStock(stockSkuLockVo);

        // 判断库存是否锁定成功
        if(r.getCode() == 0){
            // 返回值对象
            resultVo.setOrder(orderDTO.getOrderEntity());
            resultVo.setCode(0);
            // 清楚购物车数据
            redisTemplate.delete(Constants.CART_PREFIX+userInfo.getId());

            return resultVo;
        }else{
            // 库存锁定失败
            resultVo.setCode(2);
            return resultVo;
        }

    }

    /**
     * @Description: 根据订单id查询订单信息
     * @Author: hubin
     * @CreateDate: 2021/6/8 18:40
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/8 18:40
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @Override
    public PayVo getOrderById(String orderId) {

        // 创建支付vo对象
        PayVo payVo = new PayVo();

        // 根据订单id查询订单实体数据
        OrderEntity orderEntity = this.getOrderbyOrderSn(orderId);

        BigDecimal payAount = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);

        payVo.setOut_trade_no(orderId);
        payVo.setTotal_amount(payAount.toString());

        // 查询商品明细数据
        List<OrderItemEntity> orderItemEntityList = orderItemService
                .list(new QueryWrapper<OrderItemEntity>()
                        .eq("order_sn", orderId));
        // 判断
        if(orderItemEntityList!=null && orderItemEntityList.size()>0){

            // 获取一个商品明细数据
            OrderItemEntity orderItemEntity = orderItemEntityList.get(0);

            payVo.setBody(orderItemEntity.getSkuAttrsVals());
            payVo.setSubject(orderItemEntity.getSkuName());
        }



        return payVo;
    }

    /**
     * @Description: 根据订单id查询订单实体数据
     * @Author: hubin
     * @CreateDate: 2021/6/8 18:43
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/8 18:43
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    private OrderEntity getOrderbyOrderSn(String orderId) {

        OrderEntity orderEntity = this.baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderId));

        return orderEntity;
    }

    /**
     * @Description: 保存订单数据
     * @Author: hubin
     * @CreateDate: 2021/6/7 15:07
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 15:07
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    private void saveOrder(OrderDTO orderDTO) {

        // 获取订单对象
        OrderEntity orderEntity = orderDTO.getOrderEntity();
        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());

        // 保存订单
        this.baseMapper.insert(orderEntity);

        // 保存订单明细
        List<OrderItemEntity> orderItemList = orderDTO.getOrderItemList();
        // 保存订单明细的数据
        orderItemService.saveBatch(orderItemList);
    }

    /**
     * @Description: 构造订单数据
     * @Author: hubin
     * @CreateDate: 2021/6/7 11:40
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 11:40
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    private OrderDTO createOrderDTO() {

        // 验证令牌成功的，可以开始下单了，开始构建订单数据
        // 创建一个订单数据
        OrderDTO orderDTO = new OrderDTO();

        // 构造订单数据
        // 1、生成一个订单号
        String orderId = IdWorker.getTimeId();

        // 构造订单数据
        OrderEntity orderEntity = this.builderOrder(orderId);
        // 把订单数据添加到对象
        orderDTO.setOrderEntity(orderEntity);
        // 构造订单明细数据
        List<OrderItemEntity> orderItemEntityList = this.builderOrderItems(orderId);
        orderDTO.setOrderItemList(orderItemEntityList);

        // 计算整个订单总价格
        this.computePrice(orderEntity,orderItemEntityList);

        return orderDTO;
    }

    /**
     * @Description: 计算订单商品总价格
     * @Author: hubin
     * @CreateDate: 2021/6/7 11:56
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 11:56
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntityList) {


        // 订单变量，赋值价格
        BigDecimal totalPrice = new BigDecimal("0.0");

        // 优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal freight = new BigDecimal("0.0");

        // 循环订单明细
        for (OrderItemEntity orderItemEntity : orderItemEntityList) {
            coupon = coupon.add(orderItemEntity.getCouponAmount());
            promotion = promotion.add(orderItemEntity.getPromotionAmount());
            intergration = intergration.add(orderItemEntity.getIntegrationAmount());
            // 总价
            totalPrice = totalPrice.add(orderItemEntity.getRealAmount());
        }

        // 订单价格相关数据
        orderEntity.setTotalAmount(totalPrice);

        // 设置应付总金额
        orderEntity.setPayAmount(totalPrice.add(freight));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        // 删除状态
        orderEntity.setDeleteStatus(0);


    }

    /**
     * @Description: 构造订单明细
     * @Author: hubin
     * @CreateDate: 2021/6/7 11:53
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 11:53
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    private List<OrderItemEntity> builderOrderItems(String orderId) {

        // 获取登录用户信息
        UserEntity userInfo = OrderInterceptor.dtoThreadLocal.get();

        // 构造订单明细数据
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        // 从购物车中查询订单明细数据
        List<CartItemVo> cartItems = cartRemoteClient.getCartItems();
        if(cartItems!=null && cartItems.size()>0){
            orderItemEntityList =  cartItems.stream().map(item->{
                // 获取购物车明细数据，变成订单明细
                // 创建对象
                OrderItemEntity orderItemEntity = new OrderItemEntity();
                // 订单号
                orderItemEntity.setOrderSn(orderId);
                // sku信息
                orderItemEntity.setSkuId(item.getSkuId());
                orderItemEntity.setSkuName(item.getTitle());
                orderItemEntity.setSkuPic(item.getImage());
                orderItemEntity.setSkuPrice(item.getPrice());
                orderItemEntity.setSkuQuantity(item.getCount());
                orderItemEntity.setSkuAttrsVals(org.springframework.util.StringUtils.collectionToDelimitedString(item.getSkuAttr(),";"));

                // 获取spu数据：根据skuId查询spu数据
                R r1 = productRemoteClient.getSpuInfoBySkuId(item.getSkuId());
                // 获取spu数据
                SpuInfoVo spuInfoVo = r1.getData("data", new TypeReference<SpuInfoVo>() {
                });

                // 构造spu相关订单明细数据
                orderItemEntity.setSpuId(spuInfoVo.getId());
                orderItemEntity.setSpuName(spuInfoVo.getSpuName());
                orderItemEntity.setCategoryId(spuInfoVo.getCategoryId());
                orderItemEntity.setSpuBrand(spuInfoVo.getBrandName());


                // 用户名
                //orderItemEntity.setMemberUsername(userInfo.getUsername());
                // 运费
                //orderItemEntity.setFreightAmount(BigDecimal.ZERO);

                // 订单项优惠金额
                orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
                orderItemEntity.setCouponAmount(BigDecimal.ZERO);
                orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

                // 原价
                BigDecimal originPrice = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
                // 实际金额： 原价-订单优惠价格
                BigDecimal subtract = originPrice.subtract(orderItemEntity.getCouponAmount())
                        .subtract(orderItemEntity.getIntegrationAmount())
                        .subtract(orderItemEntity.getPromotionAmount());

                // 设置真实价格
                orderItemEntity.setRealAmount(subtract);

                return orderItemEntity;

            }).collect(Collectors.toList());
        }

        return orderItemEntityList;


    }

    /**
     * @Description: 构造订单数据
     * @Author: hubin
     * @CreateDate: 2021/6/7 11:42
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/7 11:42
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    private OrderEntity builderOrder(String orderId) {

        // 获取登录用户信息
        UserEntity userInfo = OrderInterceptor.dtoThreadLocal.get();

        // 2、创建一个订单实体对象，添加数据  成员id 订单id 成员名称
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(userInfo.getId());
        orderEntity.setOrderSn(orderId);
        orderEntity.setMemberUsername(userInfo.getUsername());


        // 获取对应的dto对象
        SubmitOrderDTO submitOrderDTO = submitOrderDTOThreadLocal.get();

        // 获取订单地址信息
        R r = authRemoteClient.getAddressById(submitOrderDTO.getAddrId());
        UserReceiveAddressVo userReceiveAddressVo = r.getData("userReceiveAddress", new TypeReference<UserReceiveAddressVo>() {
        });

        // 构造订单地址数据信息
        orderEntity.setReceiverName(userReceiveAddressVo.getName());
        orderEntity.setReceiverPhone(userReceiveAddressVo.getPhone());
        orderEntity.setReceiverPostCode(userReceiveAddressVo.getPostCode());
        orderEntity.setReceiverProvince(userReceiveAddressVo.getProvince());
        orderEntity.setReceiverCity(userReceiveAddressVo.getCity());
        orderEntity.setReceiverRegion(userReceiveAddressVo.getRegion());
        orderEntity.setReceiverDetailAddress(userReceiveAddressVo.getDetailAddress());

        // 构造订单状态数据
        orderEntity.setStatus(OrderStatusEnum.ORDER_NEW.getCode());
        orderEntity.setAutoConfirmDay(7); //自动确认时间
        orderEntity.setConfirmStatus(0);  //确认收货状态

        return orderEntity;

    }

}