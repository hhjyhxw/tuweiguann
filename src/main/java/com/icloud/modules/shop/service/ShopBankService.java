package com.icloud.modules.shop.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.shop.entity.ShopBank;
import com.icloud.modules.small.entity.SmallOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.shop.dao.ShopBankMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 店铺银行卡 用于店铺提现
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Service
@Transactional
public class ShopBankService extends BaseServiceImpl<ShopBankMapper,ShopBank> {

    @Autowired
    private ShopBankMapper shopBankMapper;

    @Override
    public PageUtils<ShopBank> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<ShopBank> list = shopBankMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<ShopBank> pageInfo = new PageInfo<ShopBank>(list);
        PageUtils<ShopBank> page = new PageUtils<ShopBank>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
}

