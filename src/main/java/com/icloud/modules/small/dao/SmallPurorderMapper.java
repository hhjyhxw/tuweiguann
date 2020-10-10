package com.icloud.modules.small.dao;

import com.icloud.modules.small.entity.SmallPurorder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 采购单
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-06 20:50:58
 */
public interface SmallPurorderMapper extends BaseMapper<SmallPurorder> {

	List<SmallPurorder> queryMixList(Map<String,Object> map);
}
