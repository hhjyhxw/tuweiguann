package com.icloud.modules.shop.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.shop.entity.ShopMan;
import com.icloud.modules.shop.entity.ShopStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.shop.dao.ShopStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 店铺仓库 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Service
@Transactional
public class ShopStoreService extends BaseServiceImpl<ShopStoreMapper,ShopStore> {

    @Autowired
    private ShopStoreMapper shopStoreMapper;

    @Override
    public PageUtils<ShopStore> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<ShopStore> list = shopStoreMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<ShopStore> pageInfo = new PageInfo<ShopStore>(list);
        PageUtils<ShopStore> page = new PageUtils<ShopStore>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
}

