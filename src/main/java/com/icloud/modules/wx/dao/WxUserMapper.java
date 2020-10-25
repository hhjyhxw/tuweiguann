package com.icloud.modules.wx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icloud.modules.wx.entity.WxUser;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author Mr.Z
 * @email 512162086@qq.com
 * @date 2019-11-01 21:46:51
 */
public interface WxUserMapper extends BaseMapper<WxUser> {

    public List<WxUser> queryMixList(Map<String,Object> map);
	
}
