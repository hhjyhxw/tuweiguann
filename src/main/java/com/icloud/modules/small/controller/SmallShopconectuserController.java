package com.icloud.modules.small.controller;

import java.util.Arrays;
import java.util.Map;

import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.small.entity.SmallShopconectuser;
import com.icloud.modules.small.service.SmallShopconectuserService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.sys.controller.AbstractController;


/**
 * 用户下单后 生成一条用户与店铺关联记录，如果存在则更新
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:58
 * 菜单主连接： modules/small/smallshopconectuser.html
 */
@RestController
@RequestMapping("small/smallshopconectuser")
public class SmallShopconectuserController extends AbstractController{
    @Autowired
    private SmallShopconectuserService smallShopconectuserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallshopconectuser:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallShopconectuserService.findByPage(query.getPageNum(),query.getPageSize(), query);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallshopconectuser:info")
    public R info(@PathVariable("id") Long id){
        SmallShopconectuser smallShopconectuser = (SmallShopconectuser)smallShopconectuserService.getById(id);

        return R.ok().put("smallShopconectuser", smallShopconectuser);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("small:smallshopconectuser:save")
    public R save(@RequestBody SmallShopconectuser smallShopconectuser){
        smallShopconectuserService.save(smallShopconectuser);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("small:smallshopconectuser:update")
    public R update(@RequestBody SmallShopconectuser smallShopconectuser){
        ValidatorUtils.validateEntity(smallShopconectuser);
        smallShopconectuserService.updateById(smallShopconectuser);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallshopconectuser:delete")
    public R delete(@RequestBody Long[] ids){
        smallShopconectuserService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
