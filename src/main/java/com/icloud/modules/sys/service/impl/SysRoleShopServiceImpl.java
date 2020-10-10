package com.icloud.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icloud.modules.sys.dao.SysRoleDeptDao;
import com.icloud.modules.sys.dao.SysRoleShopDao;
import com.icloud.modules.sys.entity.SysRoleDeptEntity;
import com.icloud.modules.sys.entity.SysRoleShopEntity;
import com.icloud.modules.sys.service.SysRoleDeptService;
import com.icloud.modules.sys.service.SysRoleShopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 角色与部门对应关系
 */
@Service("sysRoleShopService")
public class SysRoleShopServiceImpl extends ServiceImpl<SysRoleShopDao, SysRoleShopEntity> implements SysRoleShopService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdate(Long roleId, List<Long> shopIdList) {
		/* 先删除角色与店铺关系 */
		deleteBatch(new Long[]{roleId});

		if(shopIdList.size() == 0){
			return ;
		}

		//保存角色与店铺关系
		for(Long shopId : shopIdList){
			SysRoleShopEntity sysRoleShopEntity = new SysRoleShopEntity();
			sysRoleShopEntity.setShopId(shopId);
			sysRoleShopEntity.setRoleId(roleId);
			this.save(sysRoleShopEntity);
		}
	}

	@Override
	public List<Long> queryShopIdList(Long[] roleIds) {
		return baseMapper.queryShopIdList(roleIds);
	}


	@Override
	public int deleteBatch(Long[] roleIds){
		return baseMapper.deleteBatch(roleIds);
	}
}
