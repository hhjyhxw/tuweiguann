package com.icloud.api.small.shopkeeper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.api.dto.shopkeeper.ShopStoredto;
import com.icloud.api.dto.shopkeeper.Shopdto;
import com.icloud.api.dto.shopkeeper.ShopkeeperBinddto;
import com.icloud.basecommon.service.redis.RedisService;
import com.icloud.common.R;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.entity.ShopMan;
import com.icloud.modules.shop.entity.ShopStore;
import com.icloud.modules.shop.service.ShopManService;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.shop.service.ShopStoreService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Api("店铺中心")
@RestController
@RequestMapping("/api/shopcenter")
public class ShopCenterController {

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private ShopService shopService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ShopManService shopManService;
    @Autowired
    private ShopStoreService shopStoreService;

    /**
     * 进入店铺中心是时候需要判断
     * 判断是否是店主
     * @return
     */
    @ApiOperation(value="判断是否是店主", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/checkShopkeeper",method = {RequestMethod.GET})
    public R checkShopkeeper(@LoginUser WxUser user,@RequestParam Long shopId) {
        List<ShopMan> shopManlist = shopManService.list(new QueryWrapper<ShopMan>().eq("openid",user.getXcxopenid()).eq("shop_id",shopId));
        if(shopManlist==null || shopManlist.size()==0){
            return R.error("不是店主");
        }
        ShopMan shopMan = shopManlist.get(0);
        user.setShopMan(shopMan);
        String accessToken = httpServletRequest.getHeader("accessToken");
        redisService.set(accessToken,user,3000L);//兼容h5、APP 前端服务 登陆
        return R.ok();
    }

    /**
     * 店主店铺信息接口
     * @return
     */
    @ApiOperation(value="店主店铺信息接口", notes="")
    @RequestMapping(value = "/shopinfo",method = {RequestMethod.GET})
    public R shopinfo(@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        Shop shop = (Shop) shopService.getById(user.getShopMan().getShopId());
        return R.ok().put("shop",shop);
    }

    /**
     * 店主绑定接口
     * @return
     */
    @ApiOperation(value="店主绑定接口", notes="")
    @RequestMapping(value = "/shopmanBind",method = {RequestMethod.GET})
    public R shopmanBind(@LoginUser WxUser user, @RequestBody ShopkeeperBinddto bindVo) {
        List<ShopMan> shopManlist = shopManService.list(new QueryWrapper<ShopMan>()
                .eq("account_no",bindVo.getAccountNo())
                .eq("pwd",bindVo.getPwd())
                .eq("shop_id",bindVo.getShopId()));
        if(shopManlist==null || shopManlist.size()==0){
            return R.error("账号不存在");
        }
        ShopMan shopMan = shopManlist.get(0);
        if(StringUtil.checkStr(shopMan.getOpenid())){
            return R.error("账号已被绑定");
        }
        if("0".equals(shopMan.getStatus())){
            return R.error("账号已被停用");
        }
        shopMan.setOpenid(user.getOpenid());
        shopManService.updateById(shopMan);
        return R.ok();
    }

    /**
     * 店铺自提地址查询
     * @return
     */
    @ApiOperation(value="店铺自提地址查询", notes="")
    @RequestMapping(value = "/shopStoreAddress",method = {RequestMethod.GET})
    public R shopStoreAddress(@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        List<ShopStore> shopStorelist = shopStoreService.list(new QueryWrapper<ShopStore>()
                .eq("shop_id",user.getShopMan().getShopId()));
        if(shopStorelist!=null && shopStorelist.size()>0){
            return R.ok().put("shopStore",shopStorelist.get(0));
        }
        return R.ok().put("shopStore",null);
    }

    /**
     * 保存店铺自提地址
     * @return
     */
    @ApiOperation(value="保存店铺自提地址", notes="")
    @RequestMapping(value = "/saveStoreAddress",method = {RequestMethod.GET})
    public R saveStoreAddress(@LoginUser WxUser user,@RequestBody ShopStoredto shopStoreVo) {
        ValidatorUtils.validateEntityForFront(shopStoreVo);
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        ShopStore shopStore = new ShopStore();
        BeanUtils.copyProperties(shopStoreVo,shopStore);
        shopStoreService.saveOrUpdate(shopStore);
        return R.ok();
    }


    /**
     * 开启或者关闭店铺
     * @return
     */
    @ApiOperation(value="开启或者关闭店铺", notes="")
    @RequestMapping(value = "/updateShopSatus",method = {RequestMethod.GET})
    public R updateShopSatus(@LoginUser WxUser user,@RequestBody Shopdto shopVo) {
        ValidatorUtils.validateEntityForFront(shopVo);
        if(user.getShopMan()==null){
            return R.error("不是店主,不能操作");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用，不能操作");
        }
        if(user.getShopMan().getShopId().longValue()!=shopVo.getId().longValue()){
            return R.error("店主所属店铺与操作店铺不一致");
        }
        if("0".equals(shopVo.getStatus()) || "1".equals(shopVo.getStatus())){
            Shop shop = new Shop();
            shop.setId(shopVo.getId());
            shop.setStatus(shopVo.getStatus());
            return R.ok();
        }else{
            return R.error("状态不正确");
        }
    }

}
