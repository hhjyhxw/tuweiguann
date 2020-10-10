package com.icloud.api.small;

import com.icloud.annotation.AuthIgnore;
import com.icloud.annotation.LoginUser;
import com.icloud.basecommon.util.BaiduMapUtil;
import com.icloud.common.R;
import com.icloud.modules.bsactivity.service.BsactivityAdService;
import com.icloud.modules.small.service.SmallCategoryService;
import com.icloud.modules.wx.entity.WxUser;
import com.icloud.modules.wx.service.WxUserService;
import com.icloud.modules.wx.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("用户中心接口")
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCategoryService smallCategoryService;
    @Autowired
    private WxUserService wxUserService;

    /**
     * 用户信息
     * @return
     */
    @ApiOperation(value="用户信息", notes="")
    @RequestMapping(value = "/info",method = {RequestMethod.GET})
    @ResponseBody
    public R info(@LoginUser WxUser user) {
        user = (WxUser) wxUserService.getById(user.getId());
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);
       return R.ok().put("user",userVo);
    }

    /**
     * 用户信息
     * @return
     */
    @ApiOperation(value="获取当前用户地址", notes="")
    @RequestMapping(value = "/getNowAddress",method = {RequestMethod.GET})
    @ResponseBody
    @AuthIgnore
    public R getNowAddress(@LoginUser WxUser user, @RequestParam String longitude,String latitude) {
        String address = BaiduMapUtil.addressConvertCoord(latitude,longitude);
        return R.ok().put("address",address);
    }



}
