package com.icloud.modules.bsactivity.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.bsactivity.entity.BsactivityAd;
import com.icloud.modules.small.entity.SmallOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.bsactivity.dao.BsactivityAdMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-04-26 16:19:44
 */
@Service
@Transactional
public class BsactivityAdService extends BaseServiceImpl<BsactivityAdMapper,BsactivityAd> {

    @Autowired
    private BsactivityAdMapper bsactivityAdMapper;

    @Override
    public PageUtils<BsactivityAd> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<BsactivityAd> list = bsactivityAdMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<BsactivityAd> pageInfo = new PageInfo<BsactivityAd>(list);
        PageUtils<BsactivityAd> page = new PageUtils<BsactivityAd>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
}

