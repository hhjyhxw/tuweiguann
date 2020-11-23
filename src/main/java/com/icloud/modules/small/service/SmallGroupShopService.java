package com.icloud.modules.small.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.api.vo.QueryMycouponVo;
import com.icloud.api.vo.shopkeeper.BatchStatusVo;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.modules.small.dao.SmallGroupShopMapper;
import com.icloud.modules.small.entity.SmallGroupShop;
import com.icloud.modules.small.vo.GroupSkuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-18 09:08:35
 */
@Service
@Transactional
public class SmallGroupShopService extends BaseServiceImpl<SmallGroupShopMapper,SmallGroupShop> {

    @Autowired
    private SmallGroupShopMapper smallGroupShopMapper;

    /**
     * api 端展示
     * @param pageNo
     * @param pageSize
     * @param query
     * @return
     */
    public PageUtils<GroupSkuVo> findByFrontPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
//        List<SmallGroupShop> list = smallGroupShopMapper.selectList(new QueryWrapper<SmallGroupShop>()
//                .eq("status",1)
//                .eq("supplier_id",query.get("supplierId")));
//                .ge("gmt_start", new Date())
//                .le("gmt_end",new Date()));
        List<GroupSkuVo> list = smallGroupShopMapper.queryGroupAndSku(query);
        PageInfo<GroupSkuVo> pageInfo = new PageInfo<GroupSkuVo>(list);
        PageUtils<GroupSkuVo> page = new PageUtils<GroupSkuVo>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public PageUtils<GroupSkuVo> queryGroupAndSkuForhou(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<GroupSkuVo> list = smallGroupShopMapper.queryGroupAndSkuForhou(query);
        PageInfo<GroupSkuVo> pageInfo = new PageInfo<GroupSkuVo>(list);
        PageUtils<GroupSkuVo> page = new PageUtils<GroupSkuVo>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }


    @Override
    public PageUtils<SmallGroupShop> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);

        List<SmallGroupShop> list = smallGroupShopMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallGroupShop> pageInfo = new PageInfo<SmallGroupShop>(list);
        PageUtils<SmallGroupShop> page = new PageUtils<SmallGroupShop>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }


    public List<Long> getCategoryidList(QueryMycouponVo queryMycouponVo) {
        return smallGroupShopMapper.getCategoryidList(queryMycouponVo);
    }

    /**
     *批量更新上下架状态
     * @param batchStatusVo
     * @return
     */
    public void updateSatusBatch(BatchStatusVo batchStatusVo) {
        for (Long id:batchStatusVo.getIds()) {
            SmallGroupShop groupShop = smallGroupShopMapper.selectById(id);
            if(groupShop!=null){
                groupShop.setStatus(Integer.valueOf(batchStatusVo.getStatus()));
                smallGroupShopMapper.updateById(groupShop);
            }
        }
    }

}

