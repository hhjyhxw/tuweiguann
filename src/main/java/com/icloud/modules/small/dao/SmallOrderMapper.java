package com.icloud.modules.small.dao;

import com.icloud.basecommon.model.Query;
import com.icloud.modules.small.entity.SmallOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icloud.modules.small.vo.OrderReportVo;

import java.util.List;
import java.util.Map;

/**
 * 订单表
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
public interface SmallOrderMapper extends BaseMapper<SmallOrder> {

	List<SmallOrder> queryMixList(Map<String,Object> map);

    List<OrderReportVo> queryReportList(Query query);

    List<OrderReportVo> queryReportMonthList(Query query);
}
