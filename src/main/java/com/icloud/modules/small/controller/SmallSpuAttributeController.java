package com.icloud.modules.small.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import com.icloud.modules.small.entity.SmallSku;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.small.entity.SmallSpuAttribute;
import com.icloud.modules.small.service.SmallSpuAttributeService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;


/**
 * 商品属性
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 * 菜单主连接： modules/small/smallspuattribute.html
 */
@Slf4j
@RestController
@RequestMapping("small/smallspuattribute")
public class SmallSpuAttributeController {
    @Autowired
    private SmallSpuAttributeService smallSpuAttributeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallspu:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallSpuAttributeService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/attibutList")
    @RequiresPermissions("small:smallspu:list")
    public R attibutList(@RequestParam Long spuId){
        List<SmallSpuAttribute> list = smallSpuAttributeService.list(new QueryWrapper<SmallSpuAttribute>().eq("spu_id",spuId));
        return R.ok().put("list", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallspu:info")
    public R info(@PathVariable("id") Long id){
        SmallSpuAttribute smallSpuAttribute = (SmallSpuAttribute)smallSpuAttributeService.getById(id);
        return R.ok().put("smallSpuAttribute", smallSpuAttribute);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("small:smallspu:save")
    public R save(@RequestBody SmallSpuAttribute smallSpuAttribute){
        smallSpuAttribute.setCreateTime(new Date());
        boolean result = smallSpuAttributeService.save(smallSpuAttribute);
        log.info("save_reslut======="+result);
        return R.ok().put("smallSpuAttribute", smallSpuAttribute);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("small:smallspu:update")
    public R update(@RequestBody SmallSpuAttribute smallSpuAttribute){
        ValidatorUtils.validateEntity(smallSpuAttribute);
        smallSpuAttribute.setModifyTime(new Date());
        smallSpuAttributeService.updateById(smallSpuAttribute);
        return R.ok().put("smallSpuAttribute", smallSpuAttribute);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallspu:delete")
    public R delete(@RequestBody Long[] ids){
        smallSpuAttributeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
