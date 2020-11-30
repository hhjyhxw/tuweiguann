package com.icloud.api.small.shopkeeper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.api.dto.testdto;
import com.icloud.common.R;
import com.icloud.modules.small.entity.SmallCategory;
import com.icloud.modules.small.entity.SmallSpu;
import com.icloud.modules.small.service.SmallCategoryService;
import com.icloud.modules.small.service.SmallSpuService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api("店铺商品分类")
@RestController
@RequestMapping("/api/shopCategory")
public class ShopCategoryController {

    @Autowired
    private  SmallCategoryService smallCategoryService;
    @Autowired
    private SmallSpuService smallSpuService;

    /**
     * 店主商品分类列表
     * @return
     */
    @ApiOperation(value="店主商品分类列表", notes="")
    @RequestMapping(value = "/categoryList",method = {RequestMethod.GET})
    public R categoryList(@LoginUser WxUser user,@RequestBody Map<String,Object> params) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        //获取两级分类
        List<SmallCategory> list = smallCategoryService.list(new QueryWrapper<SmallCategory>().eq("shop_id",user.getShopMan().getShopId()));
        List<testdto.ShopCategorydto> shopCategoryVolist = list.stream().filter((item) -> (item.getParentId().equals(0l))).map(item -> {
            testdto.ShopCategorydto dto = new testdto.ShopCategorydto();
            BeanUtils.copyProperties(item,dto);
            dto.setLevel(0);
            dto.setChildList(new LinkedList<>());
            return dto;
        }).collect(Collectors.toList());
        shopCategoryVolist.forEach(p->{
            list.forEach(smallCategory->{
                if(p.getId().longValue()==smallCategory.getParentId().longValue()){
                    testdto.ShopCategorydto dto = new testdto.ShopCategorydto();
                    BeanUtils.copyProperties(smallCategory,dto);
                    dto.setLevel(1);
                    dto.setChildList(new LinkedList<>());
                    p.getChildList().add(dto);
                }
            });
        });
        return R.ok().put("list",shopCategoryVolist);
    }

    /**
     * 保存店主分类列表
     * @return
     */
    @ApiOperation(value="保存店主分类列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/saveCategory",method = {RequestMethod.POST})
    public R saveCategory(@LoginUser WxUser user,@RequestBody testdto.ShopCategorydto shopCategoryVo) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        shopCategoryVo.setShopId(user.getShopMan().getShopId());
        SmallCategory smallCategory = new SmallCategory();
        BeanUtils.copyProperties(shopCategoryVo,smallCategory);
        boolean result = smallCategoryService.saveOrUpdate(smallCategory);
        if(result){
            return R.ok().put("smallCategory",smallCategory);
        }
        return R.error("保存失败");
    }

    /**
     * 商品分类信息
     * @return
     */
    @ApiOperation(value="商品分类信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分类id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/categoryInfo",method = {RequestMethod.GET})
    public R categoryInfo(@RequestParam Long id,@LoginUser WxUser user) {
        SmallCategory smallCategory = (SmallCategory) smallCategoryService.getById(id);
        return R.ok().put("smallCategory",smallCategory);
    }

    /**
     * 删除商品分类
     * @return
     */
    @ApiOperation(value="删除商品分类", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分类id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delCategory",method = {RequestMethod.GET})
    public R delCategory(@RequestParam Long id,@LoginUser WxUser user) {
        int count =  smallSpuService.count(new QueryWrapper<SmallSpu>().eq("category_id",id));
        if(count>0){
            return R.error("有关联商品,不能删除");
        }
        boolean result = smallCategoryService.removeById(id);
        return result==true?R.ok():R.error("删除失败");
    }



}
