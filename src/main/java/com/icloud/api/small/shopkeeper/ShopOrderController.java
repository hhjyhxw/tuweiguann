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

@Api("店铺订单")
@RestController
@RequestMapping("/api/shopOrder")
public class ShopOrderController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallSkuService smallSkuService;

    /**
     * 店铺订单列表
     * @return
     */
    @ApiOperation(value="店铺订单列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/orderList",method = {RequestMethod.GET})
    @ResponseBody
    public R adsList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }


    /**
     * 订单信息
     * @return
     */
    @ApiOperation(value="订单信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/orderInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R orderInfo(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

}
