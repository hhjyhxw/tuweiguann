package com.icloud.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.icloud.common.PageUtils;
import com.icloud.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

//    public List<SysLogEntity> queryMixList(Map<String,Object> map);

    public PageUtils<SysLogEntity> findByPage(int pageNo, int pageSize, Map<String, Object> query);
}
