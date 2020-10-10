package com.icloud.modules.shop.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.shop.entity.ShopStore;
import com.icloud.modules.shop.service.ShopStoreService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;


/**
 * 店铺仓库 
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 * 菜单主连接： modules/shop/shopstore.html
 */
@RestController
@RequestMapping("shop/shopstore")
public class ShopStoreController extends AbstractController {
    @Autowired
    private ShopStoreService shopStoreService;
    @Autowired
    private ShopService shopService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shopstore:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = shopStoreService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shopstore:info")
    public R info(@PathVariable("id") Long id){
        ShopStore shopStore = (ShopStore)shopStoreService.getById(id);

        return R.ok().put("shopStore", shopStore);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("shop:shopstore:save")
    public R save(@RequestBody ShopStore shopStore){
        ValidatorUtils.validateEntity(shopStore);
        shopStore.setCreatedTime(new Date());
        shopStore.setCreatedBy(getUser().getUsername());

        Shop shop = (Shop) shopService.getById(shopStore.getShopId());
        shopStore.setDeptId(shop.getDeptId());
        shopStoreService.save(shopStore);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("shop:shopstore:update")
    public R update(@RequestBody ShopStore shopStore){
        ValidatorUtils.validateEntity(shopStore);
        shopStore.setUpdatedTime(new Date());
        shopStore.setUpdatedBy(getUser().getUsername());

        Shop shop = (Shop) shopService.getById(shopStore.getShopId());
        shopStore.setDeptId(shop.getDeptId());
        shopStoreService.updateById(shopStore);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shopstore:delete")
    public R delete(@RequestBody Long[] ids){
        shopStoreService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
