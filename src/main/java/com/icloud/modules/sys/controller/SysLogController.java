package com.icloud.modules.sys.controller;

import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.modules.sys.service.SysLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 系统日志
 *
 */
@Controller
@RequestMapping("/sys/log")
public class SysLogController {
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	@RequiresPermissions("sys:log:list")
	public R list(@RequestParam Map<String, Object> params){
//		PageUtils page = sysLogService.queryPage(params);
//		return R.ok().put("page", page);
		Query query = new Query(params);
		PageUtils page = sysLogService.findByPage(query.getPageNum(),query.getPageSize(), query);
		return R.ok().put("page", page);
	}
	
}
