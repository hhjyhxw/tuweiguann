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
import com.icloud.modules.shop.entity.ShopMan;
import com.icloud.modules.shop.service.ShopManService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;


/**
 * 店员 
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 * 菜单主连接： modules/shop/shopman.html
 */
@RestController
@RequestMapping("shop/shopman")
public class ShopManController extends AbstractController {
    @Autowired
    private ShopManService shopManService;
    @Autowired
    private ShopService shopService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shopman:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = shopManService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shopman:info")
    public R info(@PathVariable("id") Long id){
        ShopMan shopMan = (ShopMan)shopManService.getById(id);

        return R.ok().put("shopMan", shopMan);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("shop:shopman:save")
    public R save(@RequestBody ShopMan shopMan){
        ValidatorUtils.validateEntity(shopMan);
        shopMan.setCreatedTime(new Date());
        shopMan.setCreatedBy(getUser().getUsername());

        Shop shop = (Shop) shopService.getById(shopMan.getShopId());
        shopMan.setDeptId(shop.getDeptId());
        shopManService.save(shopMan);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("shop:shopman:update")
    public R update(@RequestBody ShopMan shopMan){
        ValidatorUtils.validateEntity(shopMan);
        shopMan.setUpdatedTime(new Date());
        shopMan.setUpdatedBy(getUser().getUsername());
        Shop shop = (Shop) shopService.getById(shopMan.getShopId());
        shopMan.setDeptId(shop.getDeptId());
        shopManService.updateById(shopMan);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shopman:delete")
    public R delete(@RequestBody Long[] ids){
        shopManService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
