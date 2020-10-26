package com.icloud.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icloud.annotation.DataFilter;
import com.icloud.common.Constant;
import com.icloud.common.PageUtils;
import com.icloud.common.Query;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.sys.dao.SysRoleDao;
import com.icloud.modules.sys.entity.SysDeptEntity;
import com.icloud.modules.sys.entity.SysRoleEntity;
import com.icloud.modules.sys.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;


/**
 * 角色
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
//	@Autowired
//	private SysRoleDeptService sysRoleDeptService;

	@Autowired
	private SysUserRoleService sysUserRoleService;
//	@Autowired
//	private SysDeptService sysDeptService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private SysRoleShopService sysRoleShopService;

	@Override
//	@DataFilter(subDept = true, user = false)
	public PageUtils queryPage(Map<String, Object> params) {
		String roleName = (String)params.get("roleName");

		IPage<SysRoleEntity> page = this.page(
			new Query<SysRoleEntity>().getPage(params),
			new QueryWrapper<SysRoleEntity>()
				.like(StringUtils.isNotBlank(roleName),"role_name", roleName)
				.apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

		for(SysRoleEntity sysRoleEntity : page.getRecords()){
//			SysDeptEntity sysDeptEntity = sysDeptService.getById(sysRoleEntity.getDeptId());
//			if(sysDeptEntity != null){
//				sysRoleEntity.setDeptName(sysDeptEntity.getName());
//			}
//			Object obj = shopService.getById(sysRoleEntity.getShopId());
//			if(obj!=null){
//				Shop shop = (Shop)obj;
//				sysRoleEntity.setShopName(shop.getShopName());
//			}
			Shop shop = (Shop) shopService.getById(sysRoleEntity.getShopId());
			if(shop!=null){
				sysRoleEntity.setShopName(shop.getShopName());
			}
		}

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveRole(SysRoleEntity role) {
		role.setCreateTime(new Date());
		this.save(role);

		//保存角色与菜单关系
		sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
//		sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());

		sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getShopIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysRoleEntity role) {
		this.updateById(role);

		//更新角色与菜单关系
		sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
//		sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());
		//保存角色与店铺关系
		sysRoleShopService.saveOrUpdate(role.getRoleId(), role.getShopIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] roleIds) {
		//删除角色
		this.removeByIds(Arrays.asList(roleIds));

		//删除角色与菜单关联
		sysRoleMenuService.deleteBatch(roleIds);

		//删除角色与部门关联
//		sysRoleDeptService.deleteBatch(roleIds);
		//删除角色与店铺关系
		sysRoleShopService.deleteBatch(roleIds);
		//删除角色与用户关联
		sysUserRoleService.deleteBatch(roleIds);
	}


}
