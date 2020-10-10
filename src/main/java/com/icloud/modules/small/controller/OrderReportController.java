package com.icloud.modules.small.controller;

import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import com.icloud.common.DateUtil;
import com.icloud.common.GetYearUtil;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.small.entity.SmallUserCoupon;
import com.icloud.modules.small.service.SmallOrderService;
import com.icloud.modules.small.service.SmallUserCouponService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;


/**
 * 订单日报
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 * 菜单主连接： modules/small/orderreport.html
 */
@RestController
@RequestMapping("small/orderreport")
public class OrderReportController {
    @Autowired
    private SmallOrderService smallOrderService;

    /**
     * 列表
     */
    @RequestMapping("/yearlist")
    public R yearlist(){
        return R.ok().put("yearlist", GetYearUtil.getYearList())
                .put("monthlist", GetYearUtil.getMonthList());
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:orderreport:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        String date = DateUtil.formatDate(new Date());
        if(!StringUtil.checkObj(params.get("start"))){
            query.put("start", DateUtil.getDateWithAll(date+" 00:00:00"));
        }
        if(!StringUtil.checkObj(params.get("end"))){
            query.put("end", DateUtil.getDateWithAll(date+" 23:59:59"));
        }
        PageUtils page = smallOrderService.findByPageReport(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/monthlist")
    @RequiresPermissions("small:orderreport:monthlist")
    @DataFilter
    public R monthlist(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        String year = StringUtil.checkObj(params.get("year"))?params.get("year").toString():null;
        String month = StringUtil.checkObj(params.get("month"))?params.get("month").toString():null;
        //查按照汇总查询
        String yearAndMoth = null;
        String yearonly = null;
        if(!StringUtil.checkStr(year) ){
            year = String.valueOf(DateUtil.getYear());
        }
        if(!StringUtil.checkStr(month) ){
            month = String.valueOf(DateUtil.getMonth());
        }
        if(StringUtil.checkStr(month) && StringUtil.checkStr(year)){
            year = year + "-" + month;
            yearAndMoth = year;
        }else{
            yearonly =year;
        }
        query.put("yearAndMoth",yearAndMoth);
        query.put("yearonly",yearonly);
        PageUtils page = smallOrderService.findByPageMonthReport(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


}
