package com.icloud.modules.small.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.icloud.annotation.DataFilter;
import com.icloud.annotation.SysLog;
import com.icloud.basecommon.model.Query;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.small.entity.SmallCoupon;
import com.icloud.modules.small.service.SmallCouponService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.sys.controller.AbstractController;


/**
 * 折扣券管理
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:57
 * 菜单主连接： modules/small/smallcoupon.html
 */
@RestController
@RequestMapping("small/smallcoupon")
public class SmallCouponController extends AbstractController{
    @Autowired
    private SmallCouponService smallCouponService;
    @Autowired
    private ShopService shopService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallcoupon:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallCouponService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallcoupon:info")
    public R info(@PathVariable("id") Long id){
        SmallCoupon smallCoupon = (SmallCoupon)smallCouponService.getById(id);

        return R.ok().put("smallCoupon", smallCoupon);
    }

    /**
     * 保存
     */
    @SysLog("添加优惠券")
    @RequestMapping("/save")
    @RequiresPermissions("small:smallcoupon:save")
    public R save(@RequestBody SmallCoupon smallCoupon){
        ValidatorUtils.validateEntity(smallCoupon);
        smallCoupon.setCreateTime(new Date());
        smallCouponService.save(smallCoupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改优惠券")
    @RequestMapping("/update")
    @RequiresPermissions("small:smallcoupon:update")
    public R update(@RequestBody SmallCoupon smallCoupon){
        ValidatorUtils.validateEntity(smallCoupon);
        smallCoupon.setModifyTime(new Date());
        smallCouponService.updateById(smallCoupon);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除优惠券")
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallcoupon:delete")
    public R delete(@RequestBody Long[] ids){
        smallCouponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
