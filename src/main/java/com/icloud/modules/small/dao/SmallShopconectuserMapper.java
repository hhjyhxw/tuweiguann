package com.icloud.modules.small.dao;

import com.icloud.modules.small.entity.SmallShopconectuser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 用户下单后 生成一条用户与店铺关联记录，如果存在则更新
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:58
 */
public interface SmallShopconectuserMapper extends BaseMapper<SmallShopconectuser> {

	List<SmallShopconectuser> queryMixList(Map<String,Object> map);
}
