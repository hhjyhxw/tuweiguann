package com.icloud.modules.small.dao;

import com.icloud.modules.small.entity.SmallWasteRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;

/**
 * 资金流水记录表
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-07 20:18:12
 */
public interface SmallWasteRecordMapper extends BaseMapper<SmallWasteRecord> {

	List<SmallWasteRecord> queryMixList(Map<String,Object> map);
}
