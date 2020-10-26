package com.icloud.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.common.Query;
import com.icloud.modules.shop.entity.ShopMan;
import com.icloud.modules.sys.dao.SysLogDao;
import com.icloud.modules.sys.entity.SysLogEntity;
import com.icloud.modules.sys.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

   @Autowired
   private SysLogDao sysLogDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");

        IPage<SysLogEntity> page = this.page(
            new Query<SysLogEntity>().getPage(params),
            new QueryWrapper<SysLogEntity>().like(StringUtils.isNotBlank(key),"username", key)
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils<SysLogEntity> findByPage(int pageNo, int pageSize, Map<String, Object> query){
        PageHelper.startPage(pageNo, pageSize);
        List<SysLogEntity> list = sysLogDao.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SysLogEntity> pageInfo = new PageInfo<SysLogEntity>(list);
        PageUtils<SysLogEntity> page = new PageUtils<SysLogEntity>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }


}
