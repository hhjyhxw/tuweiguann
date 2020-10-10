package com.icloud.modules.small.dao;

import com.icloud.modules.small.entity.SmallCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 折扣券管理
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:57
 */
public interface SmallCouponMapper extends BaseMapper<SmallCoupon> {

	List<SmallCoupon> queryMixList(Map<String,Object> map);
}
