package com.icloud.modules.small.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.small.entity.SmallShopconectuser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.small.dao.SmallShopconectuserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * 用户下单后 生成一条用户与店铺关联记录，如果存在则更新
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:58
 */
@Service
@Transactional
public class SmallShopconectuserService extends BaseServiceImpl<SmallShopconectuserMapper,SmallShopconectuser> {

    @Autowired
    private SmallShopconectuserMapper smallShopconectuserMapper;

    @Override
    public PageUtils<SmallShopconectuser> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<SmallShopconectuser> list = smallShopconectuserMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallShopconectuser> pageInfo = new PageInfo<SmallShopconectuser>(list);
        PageUtils<SmallShopconectuser> page = new PageUtils<SmallShopconectuser>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
}

