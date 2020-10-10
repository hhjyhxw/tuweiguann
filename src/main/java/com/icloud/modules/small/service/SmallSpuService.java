package com.icloud.modules.small.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.modules.small.dao.SmallSpuMapper;
import com.icloud.modules.small.entity.SmallSku;
import com.icloud.modules.small.entity.SmallSpu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 商品spu
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Service
@Transactional
public class SmallSpuService extends BaseServiceImpl<SmallSpuMapper,SmallSpu> {

    @Autowired
    private SmallSpuMapper smallSpuMapper;
    @Autowired
    private SmallSkuService smallSkuService;

    @Override
    public PageUtils<SmallSpu> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<SmallSpu> list = smallSpuMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallSpu> pageInfo = new PageInfo<SmallSpu>(list);
        PageUtils<SmallSpu> page = new PageUtils<SmallSpu>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public R removeBatchByIds(List<Long> asList) {
        for (Long id:asList){
            int count = smallSkuService.count(new QueryWrapper<SmallSku>().eq("spu_id",id));
            if(count>0){
                return R.error("id="+id+"的商品存在子商品，请先删除子商品");
            }
        }
        return R.ok();
    }

    public Integer incSales(Long spuId,Integer delta){
        return smallSpuMapper.incSales(spuId,delta);
    }
}

