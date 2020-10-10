package com.icloud.modules.sys.dao;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icloud.modules.sys.entity.SysRoleDeptEntity;
import com.icloud.modules.sys.entity.SysRoleShopEntity;

import java.util.List;

/**
 * 角色与店铺对应关系
 */
public interface SysRoleShopDao extends BaseMapper<SysRoleShopEntity> {
	
	/**
	 * 根据角色ID，获取店铺ID列表
	 */
	List<Long> queryShopIdList(Long[] roleIds);

	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);
}
