package com.icloud.api.small.shopkeeper;

import com.icloud.annotation.LoginUser;
import com.icloud.api.dto.shopkeeper.ShopGoodsDayreportdto;
import com.icloud.api.vo.shopkeeper.ShopGoodsDayreportvo;
import com.icloud.basecommon.model.Query;
import com.icloud.common.DateUtil;
import com.icloud.common.GetYearUtil;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.util.StringUtil;
import com.icloud.modules.small.entity.SmallOrder;
import com.icloud.modules.small.entity.SmallOrderDetail;
import com.icloud.modules.small.service.SmallOrderDetailService;
import com.icloud.modules.small.service.SmallOrderService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("店铺订单报表统计")
@RestController
@RequestMapping("/api/shopStatistic")
public class ShopStatisticController {

    @Autowired
    private SmallOrderService smallOrderService;

    @Autowired
    private SmallOrderDetailService smallOrderDetailService;

    /**
     * 列表
     */
    @RequestMapping("/yearlist")
    public R yearlist() {
        return R.ok().put("yearlist", GetYearUtil.getYearList())
                .put("monthlist", GetYearUtil.getMonthList());
    }

    /**
     * 订单日报
     *
     * @return
     */
    @ApiOperation(value = "订单日报", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "商户id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/orderDayList", method = {RequestMethod.GET})
    public R adsList(@RequestParam Long shopId, @LoginUser WxUser user) {
        if (user.getShopMan() == null) {
            return R.error("不是店主");
        } else if (user.getShopMan() != null && "0".equals(user.getShopMan().getStatus())) {
            return R.error("店主账号已被禁用");
        }

        return R.ok();
    }

    /**
     * 商品日报
     * params : 1、shopId；2、日期
     * @return
     */
    @ApiOperation(value = "商品日报", notes = "")
    @RequestMapping(value = "/goodsDayList", method = {RequestMethod.GET})
    public R goodsDayList(@RequestBody ShopGoodsDayreportdto shopGoodsDayreportVo, @LoginUser WxUser user) {
        if (user.getShopMan() == null) {
            return R.error("不是店主");
        } else if (user.getShopMan() != null && "0".equals(user.getShopMan().getStatus())) {
            return R.error("店主账号已被禁用");
        }
        Date querydate = null;
        String startTime = null;
        String endTime = null;
        String fomatday = null;
        if (StringUtil.checkStr(shopGoodsDayreportVo.getDatetime())) {
            if("-1".equals(shopGoodsDayreportVo.getDirect())){
                fomatday = DateUtil.getBeforeNDay(shopGoodsDayreportVo.getDirect(),-1);
            }else if("1".equals(shopGoodsDayreportVo.getDirect())){
                fomatday = DateUtil.getBeforeNDay(shopGoodsDayreportVo.getDirect(),1);
            }else{
                fomatday = shopGoodsDayreportVo.getDatetime();
            }
            querydate = DateUtil.getDateWithoutTime(fomatday);
        } else {
            fomatday = DateUtil.formatDate(new Date());
            querydate = DateUtil.getDateWithoutTime(fomatday);
        }
        startTime=fomatday+" 00:00:00";
        endTime=fomatday+" 23:59:59";

        shopGoodsDayreportVo.setDatetime(DateUtil.formatDate(querydate));
        shopGoodsDayreportVo.setFormatdatetime(DateUtil.getStringChineseDate(querydate));

        Map<String, Object> params = new HashMap();
        params.put("page",shopGoodsDayreportVo.getPageNum());
        params.put("limit",shopGoodsDayreportVo.getPageSize());
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        params.put("shopId",user.getShopMan().getShopId());
        Query query = new Query(params);

        PageUtils<ShopGoodsDayreportvo> page = smallOrderDetailService.findSpuDayreportPage(query.getPageNum(),query.getPageSize(), query);
        return R.ok().put("shopGoodsDayreportVo",shopGoodsDayreportVo);
    }


    /**
     * params : 1、shopId；2、年 月
     * 订单月报
     *
     * @return
     */
    @ApiOperation(value = "订单月报", notes = "")
    @RequestMapping(value = "/orderMonthList", method = {RequestMethod.GET})
    public R orderMonthList(@LoginUser WxUser user, @RequestParam Map<String, Object> params) {
        if (user.getShopMan() == null) {
            return R.error("不是店主");
        } else if (user.getShopMan() != null && "0".equals(user.getShopMan().getStatus())) {
            return R.error("店主账号已被禁用");
        }
        Query query = new Query(params);
        String year = StringUtil.checkObj(params.get("year")) ? params.get("year").toString() : null;
        String month = StringUtil.checkObj(params.get("month")) ? params.get("month").toString() : null;
        //查按照汇总查询
        String yearAndMoth = null;
        String yearonly = null;
        if (!StringUtil.checkStr(year)) {
            year = String.valueOf(DateUtil.getYear());
        }
        if (!StringUtil.checkStr(month)) {
            month = String.valueOf(DateUtil.getMonth());
        }
        if (StringUtil.checkStr(month) && StringUtil.checkStr(year)) {
            year = year + "-" + month;
            yearAndMoth = year;
        } else {
            yearonly = year;
        }
        query.put("yearAndMoth", yearAndMoth);
        query.put("yearonly", yearonly);
        query.put("shopId", user.getShopMan().getShopId());
        PageUtils page = smallOrderService.findByPageMonthReport(query.getPageNum(), query.getPageSize(), query);

        return R.ok().put("page", page);
    }


}
