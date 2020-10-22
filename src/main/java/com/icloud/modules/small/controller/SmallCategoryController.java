package com.icloud.modules.small.controller;

import com.alibaba.fastjson.JSON;
import com.icloud.annotation.DataFilter;
import com.icloud.annotation.SysLog;
import com.icloud.basecommon.model.Query;
import com.icloud.common.Constant;
import com.icloud.common.R;
import com.icloud.common.beanutils.ColaBeanUtils;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.config.DeptUtils;
import com.icloud.modules.small.entity.SmallCategory;
import com.icloud.modules.small.service.SmallCategoryService;
import com.icloud.modules.small.vo.CategoryVo;
import com.icloud.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * 商品分类
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 * 菜单主连接： modules/small/smallcategory.html
 */
@RestController
@RequestMapping("small/smallcategory")
public class SmallCategoryController extends AbstractController {
    @Autowired
    private SmallCategoryService smallCategoryService;
    @Autowired
    private DeptUtils deptUtils;
//    /**
//     * 列表
//     */
//    @RequestMapping("/list")
//    @RequiresPermissions("small:smallcategory:list")
//    public R list(@RequestParam Map<String, Object> params){
//        Query query = new Query(params);
//        PageUtils page = smallCategoryService.findByPage(query.getPageNum(),query.getPageSize(), query);
//
//
//        return R.ok().put("page", page);
//    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallcategory:list")
    public List<CategoryVo> list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SmallCategory> list = smallCategoryService.queryList(query);
        List<CategoryVo> volist = ColaBeanUtils.copyListProperties(list , CategoryVo::new, (articleEntity, articleVo) -> {
            // 回调处理
        });
        logger.info("list====="+ JSON.toJSONString(list));
        logger.info("volist====="+ JSON.toJSONString(volist));
        return volist;
    }

    /**
     * 选择部门(添加、修改菜单)
     */
    @RequestMapping("/select")
    @RequiresPermissions("small:smallcategory:update")
    public R select(@RequestParam Map<String, Object> params){
        List<SmallCategory> categoryList = smallCategoryService.queryList(params);
        if(getUserId() == Constant.SUPER_ADMIN) {
            SmallCategory root = new SmallCategory();
            root.setId(0L);
            root.setTitle("一级分类");
            root.setName("一级分类");
            root.setParentId(-1L);
            root.setOpen(true);
            categoryList.add(root);
        }
        return R.ok().put("categoryList", categoryList);
    }

    /**
     * 上级部门Id(管理员则为0)
     * 获取最上级部门id
     */
    @RequestMapping("/info")
    @RequiresPermissions("small:smallcategory:list")
    public R info(){
        long categoryId = 0;
        if(getUserId() != Constant.SUPER_ADMIN){
            List<SmallCategory> smallCategoryList = smallCategoryService.queryList(new HashMap<String, Object>());
            Long parentId = null;
            for(SmallCategory smallCategory : smallCategoryList){
                if(parentId == null){
                    parentId = smallCategory.getParentId();
                    continue;
                }

                if(parentId > smallCategory.getParentId().longValue()){
                    parentId = smallCategory.getParentId();
                }
            }
            categoryId = parentId;
        }

        return R.ok().put("id", categoryId);
    }



    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallcategory:info")
    public R info(@PathVariable("id") Long id){
        SmallCategory smallCategory = (SmallCategory)smallCategoryService.getById(id);

        return R.ok().put("smallCategory", smallCategory);
    }

    /**
     * 保存
     */
    @SysLog("添加商品分类")
    @RequestMapping("/save")
    @RequiresPermissions("small:smallcategory:save")
    public R save(@RequestBody SmallCategory smallCategory){
        smallCategory.setCreateTime(new Date());
        smallCategoryService.save(smallCategory);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改商品分类")
    @RequestMapping("/update")
    @RequiresPermissions("small:smallcategory:update")
    public R update(@RequestBody SmallCategory smallCategory){
        ValidatorUtils.validateEntity(smallCategory);
        smallCategory.setModifyTime(new Date());
        smallCategoryService.updateById(smallCategory);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除商品分类")
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallcategory:delete")
    public R delete(@RequestBody Long[] ids){
        smallCategoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
