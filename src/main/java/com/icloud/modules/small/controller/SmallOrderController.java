package com.icloud.modules.small.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import com.icloud.common.beanutils.ColaBeanUtils;
import com.icloud.modules.small.entity.SmallOrderDetail;
import com.icloud.modules.small.service.SmallOrderDetailService;
import com.icloud.modules.small.vo.OrderDetailVo;
import com.icloud.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.small.entity.SmallOrder;
import com.icloud.modules.small.service.SmallOrderService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;


/**
 * 订单表
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 * 菜单主连接： modules/small/smallorder.html
 */
@RestController
@RequestMapping("small/smallorder")
public class SmallOrderController extends AbstractController {
    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private SmallOrderDetailService smallOrderDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallorder:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallOrderService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallorder:info")
    public R info(@PathVariable("id") Long id){
//        SmallOrder smallOrder = (SmallOrder)smallOrderService.getById(id);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id",id);
        SmallOrder smallOrder = smallOrderService.queryMixList(params).get(0);//保护用户信息，店铺想信息等
        List<SmallOrderDetail> detaillist =  smallOrderDetailService.list(new QueryWrapper<SmallOrderDetail>().eq("order_id",smallOrder.getId()));
        List<OrderDetailVo> detaillistvo = ColaBeanUtils.copyListProperties(detaillist , OrderDetailVo::new, (articleEntity, articleVo) -> {
            // 回调处理
        });
        return R.ok().put("smallOrder", smallOrder).put("detailList",detaillistvo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("small:smallorder:save")
    public R save(@RequestBody SmallOrder smallOrder){
        smallOrderService.save(smallOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("small:smallorder:update")
    public R update(@RequestBody SmallOrder smallOrder){
        ValidatorUtils.validateEntity(smallOrder);
        smallOrderService.updateById(smallOrder);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallorder:delete")
    public R delete(@RequestBody Long[] ids){
        smallOrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
