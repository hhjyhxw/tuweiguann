package com.icloud.modules.small.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.api.vo.QuerySkuCategoryVo;
import com.icloud.api.vo.SkuSpuCategoryVo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.common.util.StringUtil;
import com.icloud.modules.shop.entity.ShopStore;
import com.icloud.modules.small.entity.SmallSku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.small.dao.SmallSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 商品sku
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Service
@Transactional
public class SmallSkuService extends BaseServiceImpl<SmallSkuMapper,SmallSku> {

    @Autowired
    private SmallSkuMapper smallSkuMapper;



    public PageUtils<SmallSku> listForgroupPage(int pageNo, int pageSize, List<Long>  spuIds,String title) {
        PageHelper.startPage(pageNo, pageSize);
        List<SmallSku> list = null;
        if(StringUtil.checkStr(title)){
            list = smallSkuMapper.selectList(new QueryWrapper<SmallSku>().in("spu_id",spuIds).like("title",title));
        }else{
            list = smallSkuMapper.selectList(new QueryWrapper<SmallSku>().in("spu_id",spuIds));
        }
        PageInfo<SmallSku> pageInfo = new PageInfo<SmallSku>(list);
        PageUtils<SmallSku> page = new PageUtils<SmallSku>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public List<SkuSpuCategoryVo>  getSkuAndCategoryList(QuerySkuCategoryVo querySkuCategoryVo){
        return smallSkuMapper.getSkuAndCategoryList(querySkuCategoryVo);
    }
}

