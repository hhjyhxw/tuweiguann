package com.icloud.api.small.shopkeeper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.common.R;
import com.icloud.modules.bsactivity.service.BsactivityAdService;
import com.icloud.modules.small.entity.SmallCart;
import com.icloud.modules.small.entity.SmallSku;
import com.icloud.modules.small.service.SmallCartService;
import com.icloud.modules.small.service.SmallSkuService;
import com.icloud.modules.small.service.SmallSpuService;
import com.icloud.modules.small.util.CartOrderUtil;
import com.icloud.modules.small.vo.CartTotalVo;
import com.icloud.modules.small.vo.CartVo;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("店主商品")
@RestController
@RequestMapping("/api/shopgoods")
public class ShopGoodsController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallSkuService smallSkuService;

    /**
     * 店主商品库商品列表
     * @return
     */
    @ApiOperation(value="店主商品库商品列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/goodsSpuList",method = {RequestMethod.GET})
    @ResponseBody
    public R goodsSpuList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 商品库商品保存
     * @return
     */
    @ApiOperation(value="商品库商品添加", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/saveSpu",method = {RequestMethod.GET})
    @ResponseBody
    public R addSpu(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 商品库商品信息
     * @return
     */
    @ApiOperation(value="商品库商品信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/spuInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R spuInfo(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 商品库商品删除
     * @return
     */
    @ApiOperation(value="商品库商品删除", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delSpu",method = {RequestMethod.GET})
    @ResponseBody
    public R delSpu(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }


    /**
     * 店主上架商品列表
     * @return
     */
    @ApiOperation(value="店主商商家商品列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/goodsGroupList",method = {RequestMethod.GET})
    @ResponseBody
    public R goodsGroupList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 保存上架商品
     * @return
     */
    @ApiOperation(value="保存上架商品", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/saveGroupgoods",method = {RequestMethod.GET})
    @ResponseBody
    public R saveGroupgoods(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 上架商品信息
     * @return
     */
    @ApiOperation(value="上架商品信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/groupGoodsInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R groupGoodsInfo(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 删除上架商品
     * @return
     */
    @ApiOperation(value="删除上架商品", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delGroupGoods",method = {RequestMethod.GET})
    @ResponseBody
    public R delGroupGoods(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }
}
