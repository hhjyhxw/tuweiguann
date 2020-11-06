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

@Api("订单报表统计")
@RestController
@RequestMapping("/api/shopStatistic")
public class ShopStatisticController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallSkuService smallSkuService;

    /**
     * 订单日报
     * @return
     */
    @ApiOperation(value="订单日报", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/orderDayList",method = {RequestMethod.GET})
    @ResponseBody
    public R adsList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 订单月报
     * @return
     */
    @ApiOperation(value="订单月报", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/orderMonthList",method = {RequestMethod.GET})
    @ResponseBody
    public R orderMonthList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }


}
