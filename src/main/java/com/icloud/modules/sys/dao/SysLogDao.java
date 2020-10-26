package com.icloud.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icloud.modules.sys.entity.SysLogEntity;

import java.util.List;
import java.util.Map;

/**
 * 系统日志
 *
 */
public interface SysLogDao extends BaseMapper<SysLogEntity> {

    public List<SysLogEntity> queryMixList(Map<String,Object> map);
}
