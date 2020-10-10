package com.icloud.modules.small.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.small.entity.SmallPurorderDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.small.dao.SmallPurorderDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
/**
 * 采购单明细
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-06 20:50:58
 */
@Service
@Transactional
public class SmallPurorderDetailService extends BaseServiceImpl<SmallPurorderDetailMapper,SmallPurorderDetail> {

    @Autowired
    private SmallPurorderDetailMapper smallPurorderDetailMapper;

    @Override
    public PageUtils<SmallPurorderDetail> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<SmallPurorderDetail> list = smallPurorderDetailMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallPurorderDetail> pageInfo = new PageInfo<SmallPurorderDetail>(list);
        PageUtils<SmallPurorderDetail> page = new PageUtils<SmallPurorderDetail>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
}

