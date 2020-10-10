package com.icloud.modules.small.controller;

import java.util.Arrays;
import java.util.Map;
import com.icloud.basecommon.model.Query;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.small.entity.SmallPurorder;
import com.icloud.modules.small.service.SmallPurorderService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.sys.controller.AbstractController;


/**
 * 采购单
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-06 20:50:58
 * 菜单主连接： modules/small/smallpurorder.html
 */
@RestController
@RequestMapping("small/smallpurorder")
public class SmallPurorderController extends AbstractController{
    @Autowired
    private SmallPurorderService smallPurorderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallpurorder:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallPurorderService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallpurorder:info")
    public R info(@PathVariable("id") Long id){
        SmallPurorder smallPurorder = (SmallPurorder)smallPurorderService.getById(id);

        return R.ok().put("smallPurorder", smallPurorder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("small:smallpurorder:save")
    public R save(@RequestBody SmallPurorder smallPurorder){
        smallPurorderService.save(smallPurorder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("small:smallpurorder:update")
    public R update(@RequestBody SmallPurorder smallPurorder){
        ValidatorUtils.validateEntity(smallPurorder);
        smallPurorderService.updateById(smallPurorder);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallpurorder:delete")
    public R delete(@RequestBody Long[] ids){
        smallPurorderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
