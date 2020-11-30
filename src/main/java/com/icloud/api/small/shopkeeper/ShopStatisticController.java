package com.icloud.api.small.shopkeeper;

import com.icloud.annotation.LoginUser;
import com.icloud.api.dto.shopkeeper.ShopGoodsDayreportdto;
import com.icloud.api.dto.shopkeeper.ShopOrderDayreportdto;
import com.icloud.api.vo.shopkeeper.ShopGoodsDayreportvo;
import com.icloud.api.vo.shopkeeper.ShopOrderDayreportvo;
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
     * 商品日报
     * params : 1、shopId；2、日期
     * @return
     */
    @ApiOperation(value = "商品日报", notes = "")
    @RequestMapping(value = "/goodsDayList", method = {RequestMethod.GET})
    public R goodsDayList(@RequestBody ShopGoodsDayreportdto shopGoodsDayreportdto, @LoginUser WxUser user) {
        if (user.getShopMan() == null) {
            return R.error("不是店主");
        } else if (user.getShopMan() != null && "0".equals(user.getShopMan().getStatus())) {
            return R.error("店主账号已被禁用");
        }
        Date querydate = null;
        String startTime = null;
        String endTime = null;
        String fomatday = null;
        if (StringUtil.checkStr(shopGoodsDayreportdto.getDatetime())) {
            if("-1".equals(shopGoodsDayreportdto.getDirect())){
                fomatday = DateUtil.getBeforeNDay(shopGoodsDayreportdto.getDatetime(),-1);
            }else if("1".equals(shopGoodsDayreportdto.getDirect())){
                fomatday = DateUtil.getBeforeNDay(shopGoodsDayreportdto.getDatetime(),1);
            }else{
                fomatday = shopGoodsDayreportdto.getDatetime();
            }
            querydate = DateUtil.getDateWithoutTime(fomatday);
        } else {
            fomatday = DateUtil.formatDate(new Date());
            querydate = DateUtil.getDateWithoutTime(fomatday);
        }
        startTime=fomatday+" 00:00:00";
        endTime=fomatday+" 23:59:59";

        shopGoodsDayreportdto.setDatetime(fomatday);
        shopGoodsDayreportdto.setFormatdatetime(DateUtil.getStringChineseDate(querydate));

        Map<String, Object> params = new HashMap();
        params.put("page",shopGoodsDayreportdto.getPageNum());
        params.put("limit",shopGoodsDayreportdto.getPageSize());
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        params.put("shopId",user.getShopMan().getShopId());
        Query query = new Query(params);

        PageUtils<ShopGoodsDayreportvo> page = smallOrderDetailService.findSpuDayreportPage(query.getPageNum(),query.getPageSize(), query);
        return R.ok().put("shopGoodsDayreportdto",shopGoodsDayreportdto).put("page", page);
    }


    /**
     * params : 1、shopId；2、年 月
     * 订单月报
     *
     * @return
     */
    @ApiOperation(value = "订单月报", notes = "")
    @RequestMapping(value = "/orderMonthList", method = {RequestMethod.GET})
    public R orderMonthList(@LoginUser WxUser user, @RequestBody ShopOrderDayreportdto shopOrderDayreportdto) {
        if (user.getShopMan() == null) {
            return R.error("不是店主");
        } else if (user.getShopMan() != null && "0".equals(user.getShopMan().getStatus())) {
            return R.error("店主账号已被禁用");
        }
        Date querydate = null;
        String startTime = null;
        String endTime = null;
        String fomatday = null;//获取查询日期 ：yyyy-MM
        if (StringUtil.checkStr(shopOrderDayreportdto.getDatetime())) {
            if("-1".equals(shopOrderDayreportdto.getDirect())){
                fomatday = DateUtil.formatYearMonth(
                        DateUtil.getBeforeNMoth(DateUtil.parseTimeString(shopOrderDayreportdto.getDatetime(),"yyyy-MM"),-1));
            }else if("1".equals(shopOrderDayreportdto.getDirect())){
                fomatday = DateUtil.formatYearMonth(
                        DateUtil.getBeforeNMoth(DateUtil.parseTimeString(shopOrderDayreportdto.getDatetime(),"yyyy-MM"),1));
            }else{
                fomatday = shopOrderDayreportdto.getDatetime();
            }
            querydate = DateUtil.parseTimeString(fomatday,"yyyy-MM");
        } else {
            fomatday = DateUtil.formatDate(new Date());
            querydate = DateUtil.parseTimeString(fomatday,"yyyy-MM");
        }
        startTime=DateUtil.formatTimestamp(querydate);//月初时间
        endTime=DateUtil.formatTimestamp(DateUtil.getLastDayOfMonth(querydate));//月最后一天

        shopOrderDayreportdto.setDatetime(DateUtil.formatDate(querydate));
        shopOrderDayreportdto.setFormatdatetime(DateUtil.getStringChineseMoth(querydate));

        Map<String, Object> params = new HashMap();
        params.put("page",shopOrderDayreportdto.getPageNum());
        params.put("limit",shopOrderDayreportdto.getPageSize());
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        params.put("shopId",user.getShopMan().getShopId());
        Query query = new Query(params);

        PageUtils<ShopOrderDayreportvo> page = smallOrderService.findOrderDayreportPage(query.getPageNum(),query.getPageSize(), query);
        return R.ok().put("shopOrderDayreportdto",shopOrderDayreportdto).put("page", page);
    }

//    public static void main(String[] args) {
//        Date date = new Date();
//
//        System.out.println(date);
//        String ym = DateUtil.formatYearMonth(date);
//        System.out.println(ym);
//        Date strtodate = DateUtil.parseTimeString(ym,"yyyy-MM");
//        System.out.println(strtodate);
//        System.out.println(DateUtil.formatTimestamp(strtodate));
//
//    }

}
