package com.icloud.modules.shop.dao;

import com.icloud.modules.shop.entity.ShopBank;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 店铺银行卡 用于店铺提现
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
public interface ShopBankMapper extends BaseMapper<ShopBank> {

	List<ShopBank> queryMixList(Map<String,Object> map);
}
