package com.kkb.cubemall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.user.entity.UserReceiveAddressEntity;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author ithubin
 * @email ithubin@gmail.com
 * @date 2021-05-18 11:22:47
 */
public interface UserReceiveAddressService extends IService<UserReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

