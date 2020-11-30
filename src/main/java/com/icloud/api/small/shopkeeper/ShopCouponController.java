package com.icloud.api.small.shopkeeper;

import com.icloud.annotation.LoginUser;
import com.icloud.api.util.CouponUtil;
import com.icloud.api.vo.CouponVo;
import com.icloud.api.dto.shopkeeper.BatchSortdto;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.small.entity.SmallCoupon;
import com.icloud.modules.small.service.SmallCouponService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api("店铺优惠券")
@RestController
@RequestMapping("/api/shopCoupon")
public class ShopCouponController {

    @Autowired
    private SmallCouponService smallCouponService;

    /**
     * 店铺优惠券列表
     * @return
     */
    @ApiOperation(value="店铺优惠券列表", notes="")
    @RequestMapping(value = "/couponList",method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页多少记录", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long"),
    })
    public R adsList(@LoginUser WxUser user,String pageNum,String pageSize) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        Query query = new Query(new HashMap<>());
        query.put("status",1);
        query.put("shopId",user.getShopMan().getShopId());
        PageUtils page = smallCouponService.findByPage(StringUtil.checkStr(pageNum)?Integer.parseInt(pageNum):1,
                StringUtil.checkStr(pageSize)?Integer.parseInt(pageSize):10,
                query);
        List<SmallCoupon> list = (List<SmallCoupon>) page.getList();
        List<CouponVo> couponVoList = new ArrayList<CouponVo>();
        if(list!=null && list.size()>0){
            list.forEach(p->{
                CouponVo couponVo = CouponUtil.getCouponvo(p);
                couponVoList.add(couponVo);
            });
        }
        page.setList(couponVoList);
        return R.ok().put("page", page);
    }

    /**
     * 保存广告
     * @return
     */
    @ApiOperation(value="保存店铺优惠券", notes="")
    @RequestMapping(value = "/saveCoupon",method = {RequestMethod.POST})
    @ResponseBody
    public R saveAds(@LoginUser WxUser user, @RequestBody SmallCoupon smallCoupon) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        ValidatorUtils.validateEntity(smallCoupon);
        smallCoupon.setCreateTime(new Date());
        boolean result = smallCouponService.save(smallCoupon);
        return result==true?R.ok().put("smallCoupon",smallCoupon):R.error("添加失败");
    }
    /**
     * 修改店铺优惠券
     * @return
     */

    @ApiOperation(value="修改店铺优惠券", notes="")
    @RequestMapping(value = "/updateCoupon",method = {RequestMethod.POST})
    @ResponseBody
    public R updateAds(@LoginUser WxUser user, @RequestBody SmallCoupon smallCoupon) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        ValidatorUtils.validateEntity(smallCoupon);
        smallCoupon.setModifyTime(new Date());
        boolean result = smallCouponService.updateById(smallCoupon);
        return result==true?R.ok().put("smallCoupon",smallCoupon):R.error("更新失败");
    }

    /**
     * 优惠券
     * @return
     */
    @ApiOperation(value="优惠券", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "优惠券id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/adsInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R adsInfo(@LoginUser WxUser user,@RequestParam Long id) {
        SmallCoupon smallCoupon = (SmallCoupon) smallCouponService.getById(id);
        return R.ok().put("smallCoupon",smallCoupon);
    }

    /**
     * 删除优惠券
     * @return
     */
    @ApiOperation(value="删除优惠券", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "优惠券id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delAds",method = {RequestMethod.GET})
    @ResponseBody
    public R delAds(@RequestParam Long id,@LoginUser WxUser user) {
        boolean result = smallCouponService.removeById(id);
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
        smallCouponService.updateSortBatch(batchSortVo);
        return R.ok();
    }

}
