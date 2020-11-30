package com.icloud.modules.small.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.api.vo.shopkeeper.ShopGoodsDayreportvo;
import com.icloud.basecommon.model.Query;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.shop.entity.ShopMan;
import com.icloud.modules.small.entity.SmallOrderDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.small.dao.SmallOrderDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 订单明细
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Service
@Transactional
public class SmallOrderDetailService extends BaseServiceImpl<SmallOrderDetailMapper,SmallOrderDetail> {

    @Autowired
    private SmallOrderDetailMapper smallOrderDetailMapper;

    public PageUtils<ShopGoodsDayreportvo> findSpuDayreportPage(int pageNum, int pageSize, Query query) {
        PageHelper.startPage(pageNum, pageSize);
        List<ShopGoodsDayreportvo> list = smallOrderDetailMapper.findSpuDayreportPage(query);
        PageInfo<ShopGoodsDayreportvo> pageInfo = new PageInfo<ShopGoodsDayreportvo>(list);
        PageUtils<ShopGoodsDayreportvo> page = new PageUtils<ShopGoodsDayreportvo>(list,(int)pageInfo.getTotal(),pageSize,pageNum);
        return page;
    }
}

