package com.icloud.api.util;

import com.icloud.api.vo.CouponVo;
import com.icloud.common.DateUtil;
import com.icloud.modules.small.entity.SmallCoupon;

import java.util.ArrayList;
import java.util.List;

public class CouponUtil {
    public static List<CouponVo> getCouponvoList(List<SmallCoupon> list){
        List<CouponVo> volist = new ArrayList<CouponVo>();
        return volist;
    }

    public static CouponVo getCouponvo(SmallCoupon smallCoupon){
        CouponVo vo = new CouponVo();
        vo.setId(smallCoupon.getId());
        vo.setShopId(smallCoupon.getShopId());
        if(smallCoupon.getReceivedStatus().intValue()==0){
            vo.setReceivedStatus(0);
            //通用券
            if(smallCoupon.getSurplus().intValue()==0){
                vo.setColor("#9F6DFA");
                vo.setLtBg("#FFFFFF");
                vo.setHeight("180rpx");
                vo.setUnit("￥");
                vo.setNumber(smallCoupon.getDiscount().toString());
                vo.setTxt("满"+smallCoupon.getMin()+"可用");
                vo.setTitle(smallCoupon.getTitle());
                vo.setDesc("有效期"+DateUtil.formatDate(smallCoupon.getEndTime()));
                vo.setBtn("领券");
                vo.setDrawed("已抢"+smallCoupon.getFreezeStore()+"张");
            }else{
                //专用券
                vo.setColor("#FF8830");
                vo.setLtBg("#FFFFFF");
                vo.setHeight("180rpx");
                vo.setUnit("￥");
                vo.setNumber(smallCoupon.getDiscount().toString());
                vo.setTxt("满"+smallCoupon.getMin()+"可用");
                vo.setTitle(smallCoupon.getTitle());
                vo.setDesc("有效期"+DateUtil.formatDate(smallCoupon.getEndTime()));
                vo.setBtn("领券");
                vo.setDrawed("已抢"+smallCoupon.getFreezeStore()+"张");
            }
        }else{
            //已领取
            vo.setReceivedStatus(1);
            vo.setColor("#FF3456");
            vo.setLtBg("#FFFFFF");
            vo.setHeight("180rpx");
            vo.setUnit("￥");
            vo.setNumber(smallCoupon.getDiscount().toString());
            vo.setTxt("满"+smallCoupon.getMin()+"可用");
            vo.setTitle(smallCoupon.getTitle());
            vo.setDesc("有效期"+DateUtil.formatDate(smallCoupon.getEndTime()));
            vo.setBtn("已领取");
            vo.setDrawed("已抢"+smallCoupon.getFreezeStore()+"张");
        }
        return vo;
    }
}
