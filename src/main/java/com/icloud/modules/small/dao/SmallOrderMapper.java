package com.icloud.modules.small.dao;

import com.icloud.api.vo.shopkeeper.ShopOrderDayreportvo;
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

    /**
     * 订单日报
     * @param query
     * @return
     */
    List<OrderReportVo> queryReportList(Query query);

    /**
     * 订单月报
     * @param query
     * @return
     */
    List<OrderReportVo> queryReportMonthList(Query query);

    /*订单月报明细*/
    List<SmallOrder> queryReportMonthDetailList(Map<String,Object> map);



    /*移动端店主 单月报明细*/
    List<ShopOrderDayreportvo> findOrderDayreportPage(Map<String,Object> map);


}
