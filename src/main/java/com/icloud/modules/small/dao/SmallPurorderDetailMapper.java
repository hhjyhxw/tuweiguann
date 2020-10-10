package com.icloud.modules.small.dao;

import com.icloud.modules.small.entity.SmallPurorderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 采购单明细
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-06 20:50:58
 */
public interface SmallPurorderDetailMapper extends BaseMapper<SmallPurorderDetail> {

	List<SmallPurorderDetail> queryMixList(Map<String,Object> map);
}
