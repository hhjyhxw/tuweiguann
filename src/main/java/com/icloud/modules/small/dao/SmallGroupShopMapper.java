package com.icloud.modules.small.dao;

import com.icloud.modules.small.entity.SmallGroupShop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icloud.modules.small.vo.GroupSkuVo;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-18 09:08:35
 */
public interface SmallGroupShopMapper extends BaseMapper<SmallGroupShop> {

	List<SmallGroupShop> queryMixList(Map<String,Object> map);

	List<GroupSkuVo> queryGroupAndSku(Map<String,Object> map);

	List<GroupSkuVo> queryGroupAndSkuForhou(Map<String,Object> map);
}
