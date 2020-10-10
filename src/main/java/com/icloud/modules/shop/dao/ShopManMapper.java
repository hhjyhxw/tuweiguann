package com.icloud.modules.shop.dao;

import com.icloud.modules.shop.entity.ShopMan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 店员 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
public interface ShopManMapper extends BaseMapper<ShopMan> {

	List<ShopMan> queryMixList(Map<String,Object> map);
}
