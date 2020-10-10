package com.icloud.modules.shop.dao;

import com.icloud.modules.shop.entity.ShopTradeDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 店铺账号交易明细 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
public interface ShopTradeDetailsMapper extends BaseMapper<ShopTradeDetails> {

	List<ShopTradeDetails> queryMixList(Map<String,Object> map);
}
