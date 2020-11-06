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

@Api("体现管理")
@RestController
@RequestMapping("/api/shopWithdraw")
public class ShopWithdrawController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallSkuService smallSkuService;

    /**
     * 提现申请
     * @return
     */
    @ApiOperation(value="提现申请", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/applyDraw",method = {RequestMethod.GET})
    @ResponseBody
    public R applyDraw(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 提现记录
     * @return
     */
    @ApiOperation(value="提现记录", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/drawList",method = {RequestMethod.GET})
    @ResponseBody
    public R drawList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }


    /**
     * 提现记录明细
     * @return
     */
    @ApiOperation(value="提现记录明细", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/drawDetailInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R drawDetailinfo(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 资金流水记录
     * @return
     */
    @ApiOperation(value="资金流水记录", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/capitalFlowList",method = {RequestMethod.GET})
    @ResponseBody
    public R capitalFlowList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 资金流水记录明细
     * @return
     */
    @ApiOperation(value="资金流水记录明细", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/flowDetailInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R flowDetailInfo(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

}
