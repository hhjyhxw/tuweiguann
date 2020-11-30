package com.icloud.api.small.shopkeeper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.api.dto.shopkeeper.BatchSortdto;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.bsactivity.entity.BsactivityAd;
import com.icloud.modules.bsactivity.service.BsactivityAdService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Api("店铺广告")
@RestController
@RequestMapping("/api/shopkeeper/shopAds")
public class ShopAdsController {

    @Autowired
    private BsactivityAdService bsactivityAdService;

    /**
     * 店铺广告列表
     * @return
     */
    @ApiOperation(value="店铺广告列表", notes="")
    @RequestMapping(value = "/adsList",method = {RequestMethod.GET})
    @ResponseBody
    public R adsList(@LoginUser WxUser user,@RequestBody Map<String,Object> params) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        List<BsactivityAd> list = bsactivityAdService.list(new QueryWrapper<BsactivityAd>().eq("shop_id",user.getShopMan().getShopId()));
        return R.ok().put("list",list);
    }

    /**
     * 保存广告
     * @return
     */
    @ApiOperation(value="保存广告", notes="")
    @RequestMapping(value = "/saveAds",method = {RequestMethod.POST})
    @ResponseBody
    public R saveAds(@LoginUser WxUser user, @RequestBody BsactivityAd bsactivityAd) {
        ValidatorUtils.validateEntity(bsactivityAd);
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        if(bsactivityAd.getSortNum()==null){
            bsactivityAd.setSortNum(999);
        }
        bsactivityAd.setShopId(user.getShopMan().getShopId());
        bsactivityAd.setCreateTime(new Date());
        bsactivityAd.setCreateOperator(user.getShopMan().getAccountNo());
        boolean result = bsactivityAdService.save(bsactivityAd);
        return result==true?R.ok().put("bsactivityAd",bsactivityAd):R.error("添加失败");
    }
    /**
     * 保存广告列表
     * @return
     */

    @ApiOperation(value="修改广告", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/updateAds",method = {RequestMethod.POST})
    @ResponseBody
    public R updateAds(@LoginUser WxUser user, @RequestBody BsactivityAd bsactivityAd) {
        ValidatorUtils.validateEntity(bsactivityAd);
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        ValidatorUtils.validateEntity(bsactivityAd);
        bsactivityAd.setModifyTime(new Date());
        bsactivityAd.setModifyOperator(user.getShopMan().getAccountNo());
        boolean result = bsactivityAdService.updateById(bsactivityAd);
        return result==true?R.ok().put("bsactivityAd",bsactivityAd):R.error("更新失败");
    }

    /**
     * 广告信息
     * @return
     */
    @ApiOperation(value="广告信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "广告id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/adsInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R adsInfo(@LoginUser WxUser user,@RequestParam Long id) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        BsactivityAd bsactivityAd = (BsactivityAd) bsactivityAdService.getById(id);
        return R.ok().put("bsactivityAd",bsactivityAd);
    }

    /**
     * 删除广告
     * @return
     */
    @ApiOperation(value="删除广告", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "广告id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delAds",method = {RequestMethod.GET})
    @ResponseBody
    public R delAds(@RequestParam Long id,@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        boolean result = bsactivityAdService.removeById(id);
        return result==true?R.ok():R.error("删除失败");
    }

    /**
     * 批量更新排序
     * @return
     */
    @ApiOperation(value="批量更新排序", notes="")
    @RequestMapping(value = "/updateSortBatch",method = {RequestMethod.POST})
    @ResponseBody
    public R updateSortBatch(@RequestBody BatchSortdto batchSortVo, @LoginUser WxUser user) {
        if(batchSortVo.getIds().length==0){
            return R.error("ids不能为空");
        }
        if(batchSortVo.getIds().length==0){
            return R.error("sortNum不能为空");
        }
        bsactivityAdService.updateSortBatch(batchSortVo);
        return R.ok();
    }

}
