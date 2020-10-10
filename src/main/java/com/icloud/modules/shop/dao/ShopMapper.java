package com.icloud.modules.shop.dao;

import com.icloud.modules.shop.entity.Shop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 店铺 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
public interface ShopMapper extends BaseMapper<Shop> {

	List<Shop> queryMixList(Map<String,Object> map);
}
