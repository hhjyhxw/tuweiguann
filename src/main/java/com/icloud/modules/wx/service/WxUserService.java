package com.icloud.modules.wx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.small.dao.SmallOrderMapper;
import com.icloud.modules.small.entity.SmallSpu;
import com.icloud.modules.small.entity.SmallUserCoupon;
import com.icloud.modules.wx.dao.WxUserMapper;
import com.icloud.modules.wx.entity.WxUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Mr.Z
 * @email 512162086@qq.com
 * @date 2019-11-01 21:46:51
 */
@Service
@Transactional
public class WxUserService extends BaseServiceImpl<WxUserMapper, WxUser> {
    @Autowired
    private WxUserMapper wxUserMapper;

    public WxUser findByOpenId(String openId) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<WxUser>();
        queryWrapper.eq("openid",openId);
        return (WxUser) getOne(queryWrapper);
    }
    public WxUser findByXcxopenid(String xcxopenid) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<WxUser>();
        queryWrapper.eq("xcxopenid",xcxopenid);
        return (WxUser) getOne(queryWrapper);
    }



    @Override
    public PageUtils<WxUser> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<WxUser> list = wxUserMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<WxUser> pageInfo = new PageInfo<WxUser>(list);
        PageUtils<WxUser> page = new PageUtils<WxUser>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public List<WxUser> queryMixList(Map<String,Object> map){
        return wxUserMapper.queryMixList(map);
    }
}