package com.icloud.modules.small.dao;

import com.icloud.api.vo.QueryMycouponVo;
import com.icloud.modules.small.entity.SmallUserCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icloud.modules.small.vo.MycouponVo;

import java.util.List;
import java.util.Map;

/**
 * 用户拥有的折扣券
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
public interface SmallUserCouponMapper extends BaseMapper<SmallUserCoupon> {

	List<SmallUserCoupon> queryMixList(Map<String,Object> map);

    /*查询我的某个店铺的 未使用的优惠券 我的优惠券列表*/
    List<MycouponVo> queryMixListVo(Map<String, Object> map);

    /*支付页面 查询我的某个店铺的 未使用的优惠券*/
    List<MycouponVo> getMycouponList(List<Long> categoryIds,Long userId,Long shopId);
}
