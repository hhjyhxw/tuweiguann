package com.icloud.modules.shop.controller;

import java.util.Arrays;
import java.util.Map;

import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import com.icloud.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.shop.entity.ShopTradeDetails;
import com.icloud.modules.shop.service.ShopTradeDetailsService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;


/**
 * 店铺账号交易明细 
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 * 菜单主连接： modules/shop/shoptradedetails.html
 */
@RestController
@RequestMapping("shop/shoptradedetails")
public class ShopTradeDetailsController extends AbstractController {
    @Autowired
    private ShopTradeDetailsService shopTradeDetailsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shoptradedetails:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = shopTradeDetailsService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shoptradedetails:info")
    public R info(@PathVariable("id") Long id){
        ShopTradeDetails shopTradeDetails = (ShopTradeDetails)shopTradeDetailsService.getById(id);

        return R.ok().put("shopTradeDetails", shopTradeDetails);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("shop:shoptradedetails:save")
    public R save(@RequestBody ShopTradeDetails shopTradeDetails){

        shopTradeDetailsService.save(shopTradeDetails);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("shop:shoptradedetails:update")
    public R update(@RequestBody ShopTradeDetails shopTradeDetails){
        ValidatorUtils.validateEntity(shopTradeDetails);
        shopTradeDetailsService.updateById(shopTradeDetails);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shoptradedetails:delete")
    public R delete(@RequestBody Long[] ids){
        shopTradeDetailsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
