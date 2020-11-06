package com.icloud.api.small.shopkeeper;


import com.icloud.annotation.LoginUser;
import com.icloud.common.R;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@Api("店主中心")
@RestController
@RequestMapping("/api/shopcenter")
public class ShopCenterController {

    /**
     * 店主信息接口
     * @return
     */
    @ApiOperation(value="店主信息接口", notes="")
    @RequestMapping(value = "/shopinfo",method = {RequestMethod.GET})
    @ResponseBody
    public R shopinfo(@LoginUser WxUser user) {

        return R.ok().put("user",user);
    }

    /**
     * 店主绑定接口
     * @return
     */
    @ApiOperation(value="店主绑定接口", notes="")
    @RequestMapping(value = "/shopmanBind",method = {RequestMethod.GET})
    @ResponseBody
    public R shopmanBind(@LoginUser WxUser user) {

        return R.ok().put("user",user);
    }

}
