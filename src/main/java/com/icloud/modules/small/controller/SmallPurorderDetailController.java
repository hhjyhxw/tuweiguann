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
import com.icloud.modules.small.entity.SmallPurorderDetail;
import com.icloud.modules.small.service.SmallPurorderDetailService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.sys.controller.AbstractController;


/**
 * 采购单明细
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-06 20:50:58
 * 菜单主连接： modules/small/smallpurorderdetail.html
 */
@RestController
@RequestMapping("small/smallpurorderdetail")
public class SmallPurorderDetailController extends AbstractController{
    @Autowired
    private SmallPurorderDetailService smallPurorderDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallpurorderdetail:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallPurorderDetailService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallpurorderdetail:info")
    public R info(@PathVariable("id") Long id){
        SmallPurorderDetail smallPurorderDetail = (SmallPurorderDetail)smallPurorderDetailService.getById(id);

        return R.ok().put("smallPurorderDetail", smallPurorderDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("small:smallpurorderdetail:save")
    public R save(@RequestBody SmallPurorderDetail smallPurorderDetail){
        smallPurorderDetailService.save(smallPurorderDetail);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("small:smallpurorderdetail:update")
    public R update(@RequestBody SmallPurorderDetail smallPurorderDetail){
        ValidatorUtils.validateEntity(smallPurorderDetail);
        smallPurorderDetailService.updateById(smallPurorderDetail);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallpurorderdetail:delete")
    public R delete(@RequestBody Long[] ids){
        smallPurorderDetailService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
