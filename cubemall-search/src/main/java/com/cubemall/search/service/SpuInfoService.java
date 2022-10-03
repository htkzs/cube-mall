package com.cubemall.search.service;

import com.kkb.cubemall.common.utils.R;

import java.io.IOException;
import java.util.Map;

public interface SpuInfoService {
    /**
     * 获取增量的Spu数据
     * @param spuId
     * @return
     */
    public R getSpuInfo(Long spuId);

    public R getAllSpuInfo();

    public Map<String,Object> search(Map<String,String> paramMap) throws IOException;
}
