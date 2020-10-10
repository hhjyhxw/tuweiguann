package com.icloud.modules.small.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.api.vo.QueryMycouponVo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.exceptions.ApiException;
import com.icloud.modules.small.entity.SmallCoupon;
import com.icloud.modules.small.entity.SmallSpu;
import com.icloud.modules.small.entity.SmallUserCoupon;
import com.icloud.modules.small.vo.MycouponVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.small.dao.SmallUserCouponMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 用户拥有的折扣券
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Service
@Transactional
public class SmallUserCouponService extends BaseServiceImpl<SmallUserCouponMapper,SmallUserCoupon> {

    @Autowired
    private SmallUserCouponMapper smallUserCouponMapper;
    @Autowired
    private SmallCouponService smallCouponService;

    @Override
    public PageUtils<SmallUserCoupon> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);

        List<SmallUserCoupon> list = smallUserCouponMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallUserCoupon> pageInfo = new PageInfo<SmallUserCoupon>(list);
        PageUtils<SmallUserCoupon> page = new PageUtils<SmallUserCoupon>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public PageUtils<MycouponVo> findByPageVo(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<MycouponVo> list = smallUserCouponMapper.queryMixListVo(query);
        PageInfo<MycouponVo> pageInfo = new PageInfo<MycouponVo>(list);
        PageUtils<MycouponVo> page = new PageUtils<MycouponVo>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public boolean addUserCoupon(SmallUserCoupon userCoupon, SmallCoupon smallCoupon) {
       int result = smallUserCouponMapper.insert(userCoupon);
       if(result==1){
           Integer freezeStore = smallCoupon.getFreezeStore();
           smallCoupon.setFreezeStore(freezeStore==null?1:freezeStore+1);
           boolean flag = smallCouponService.updateById(smallCoupon);
           if(!flag){
               throw new ApiException("领取失败");
           }
       }else{
           return false;
       }
        return true;
    }

    public List<MycouponVo> getCategoryidList(QueryMycouponVo queryMycouponVo) {
        return smallUserCouponMapper.getCategoryidList(queryMycouponVo);
    }
}

