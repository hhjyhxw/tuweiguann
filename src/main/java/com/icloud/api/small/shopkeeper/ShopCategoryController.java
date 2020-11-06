package com.icloud.api.small.shopkeeper;

import com.icloud.annotation.LoginUser;
import com.icloud.common.R;
import com.icloud.modules.bsactivity.service.BsactivityAdService;
import com.icloud.modules.small.service.SmallCartService;
import com.icloud.modules.small.service.SmallSkuService;
import com.icloud.modules.small.service.SmallSpuService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("店主商品分类")
@RestController
@RequestMapping("/api/shopCategory")
public class ShopCategoryController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallSkuService smallSkuService;

    /**
     * 店主商品分类列表
     * @return
     */
    @ApiOperation(value="店主商品分类列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/categoryList",method = {RequestMethod.GET})
    @ResponseBody
    public R categoryList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 保存店主分类列表
     * @return
     */
    @ApiOperation(value="保存店主分类列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/saveCategory",method = {RequestMethod.GET})
    @ResponseBody
    public R saveCategory(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 商品分类信息
     * @return
     */
    @ApiOperation(value="商品分类信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/categoryInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R categoryInfo(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 删除商品分类
     * @return
     */
    @ApiOperation(value="删除商品分类", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delCategory",method = {RequestMethod.GET})
    @ResponseBody
    public R delCategory(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }



}
