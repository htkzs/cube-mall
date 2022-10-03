package com.kkb.cubemall.user.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;

import com.kkb.cubemall.user.dao.UserReceiveAddressDao;
import com.kkb.cubemall.user.entity.UserReceiveAddressEntity;
import com.kkb.cubemall.user.service.UserReceiveAddressService;


@Service("userReceiveAddressService")
public class UserReceiveAddressServiceImpl extends ServiceImpl<UserReceiveAddressDao, UserReceiveAddressEntity> implements UserReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserReceiveAddressEntity> page = this.page(
                new Query<UserReceiveAddressEntity>().getPage(params),
                new QueryWrapper<UserReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

}