package com.icloud.modules.small.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.api.dto.shopkeeper.BatchSortdto;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.small.dao.SmallCouponMapper;
import com.icloud.modules.small.entity.SmallCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 折扣券管理
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:57
 */
@Service
@Transactional
public class SmallCouponService extends BaseServiceImpl<SmallCouponMapper,SmallCoupon> {

    @Autowired
    private SmallCouponMapper smallCouponMapper;

    @Override
    public PageUtils<SmallCoupon> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<SmallCoupon> list = smallCouponMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallCoupon> pageInfo = new PageInfo<SmallCoupon>(list);
        PageUtils<SmallCoupon> page = new PageUtils<SmallCoupon>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public void updateSortBatch(BatchSortdto batchSortVo) {
        for (int i=0;i<batchSortVo.getIds().length;i++){
            SmallCoupon smallCoupon = smallCouponMapper.selectById(batchSortVo.getSortNum()[i]);
            if(smallCoupon!=null){
                smallCoupon.setOrderNum(batchSortVo.getSortNum()[i]);
                smallCouponMapper.updateById(smallCoupon);
            }
        }
    }

}

