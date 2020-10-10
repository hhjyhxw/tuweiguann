package com.icloud.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icloud.modules.sys.entity.SysRoleDeptEntity;
import com.icloud.modules.sys.entity.SysRoleShopEntity;

import java.util.List;


/**
 * 角色与店铺对应关系
 */
public interface SysRoleShopService extends IService<SysRoleShopEntity> {
	
	void saveOrUpdate(Long roleId, List<Long> shopIdList);
	
	/**
	 * 根据角色ID，获取店铺ID列表
	 */
	List<Long> queryShopIdList(Long[] roleIds) ;

	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);
}
