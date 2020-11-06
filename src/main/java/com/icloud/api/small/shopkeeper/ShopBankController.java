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

@Api("银行卡")
@RestController
@RequestMapping("/api/shopBank")
public class ShopBankController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallSkuService smallSkuService;

    /**
     * 银行卡列表
     * @return
     */
    @ApiOperation(value="银行卡列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/bankList",method = {RequestMethod.GET})
    @ResponseBody
    public R bankList(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 保存银行卡
     * @return
     */
    @ApiOperation(value="保存银行卡", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/saveBank",method = {RequestMethod.GET})
    @ResponseBody
    public R saveBank(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 银行卡信息
     * @return
     */
    @ApiOperation(value="银行卡信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/bankInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R bankInfo(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }

    /**
     * 删除银行卡
     * @return
     */
    @ApiOperation(value="删除银行卡", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delBank",method = {RequestMethod.GET})
    @ResponseBody
    public R delBank(@RequestParam Long shopId,@LoginUser WxUser user) {
        return R.ok();
    }



}
