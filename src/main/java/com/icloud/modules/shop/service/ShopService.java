package com.icloud.modules.shop.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.entity.ShopBank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.shop.dao.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 店铺 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Service
@Transactional
public class ShopService extends BaseServiceImpl<ShopMapper,Shop> {

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public PageUtils<Shop> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<Shop> list = shopMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<Shop> pageInfo = new PageInfo<Shop>(list);
        PageUtils<Shop> page = new PageUtils<Shop>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

}

