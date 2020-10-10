package com.icloud.modules.shop.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.shop.entity.ShopBank;
import com.icloud.modules.shop.entity.ShopMan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.shop.dao.ShopManMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 店员 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Service
@Transactional
public class ShopManService extends BaseServiceImpl<ShopManMapper,ShopMan> {

    @Autowired
    private ShopManMapper shopManMapper;

    @Override
    public PageUtils<ShopMan> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<ShopMan> list = shopManMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<ShopMan> pageInfo = new PageInfo<ShopMan>(list);
        PageUtils<ShopMan> page = new PageUtils<ShopMan>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
}

