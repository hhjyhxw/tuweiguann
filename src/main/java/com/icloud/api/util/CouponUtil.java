package com.icloud.api.util;

import com.icloud.api.vo.CouponVo;
import com.icloud.common.DateUtil;
import com.icloud.modules.small.entity.SmallCoupon;
import com.icloud.modules.small.vo.MycouponVo;

import java.util.ArrayList;
import java.util.List;

public class CouponUtil {
    public static List<CouponVo> getCouponvoList(List<SmallCoupon> list){
        List<CouponVo> volist = new ArrayList<CouponVo>();
        return volist;
    }

    /**
     * 获取店铺优惠券
     * @param smallCoupon
     * @return
     */
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
                vo.setNumber(String.valueOf(smallCoupon.getDiscount().intValue()));
                vo.setMin(String.valueOf(smallCoupon.getMin().intValue()));
                vo.setTxt("满"+(smallCoupon.getMin().intValue())+"可用");
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
                vo.setNumber(String.valueOf(smallCoupon.getDiscount().intValue()));
                vo.setMin(String.valueOf(smallCoupon.getMin().intValue()));
                vo.setTxt("满"+(smallCoupon.getMin().intValue())+"可用");
                vo.setTitle(smallCoupon.getTitle());
                vo.setDesc("有效期"+DateUtil.formatDate(smallCoupon.getEndTime()));
                vo.setBtn("领券");
                vo.setDrawed("已抢"+smallCoupon.getFreezeStore()+"张");
            }
        }else{
            //已领取
            vo.setReceivedStatus(1);
            vo.setColor("#FF3456");
//            vo.setLtBg("#FFFFFF");
            vo.setLtBg("lightgrey");
            vo.setHeight("180rpx");
            vo.setUnit("￥");
            vo.setNumber(String.valueOf(smallCoupon.getDiscount().intValue()));
            vo.setMin(String.valueOf(smallCoupon.getMin().intValue()));
            vo.setTxt("满"+(smallCoupon.getMin().intValue())+"可用");
            vo.setTitle(smallCoupon.getTitle());
            vo.setDesc("有效期"+DateUtil.formatDate(smallCoupon.getEndTime()));
            vo.setBtn("已领取");
            vo.setDrawed("已抢"+smallCoupon.getFreezeStore()+"张");
        }
        return vo;
    }


    /**
     * 我的优惠券列表
     * 状态：未使用、已使用、已过期
     * @param mycouponVo
     * @return
     */
    public static CouponVo getMyCouponvo(MycouponVo mycouponVo){
        CouponVo vo = new CouponVo();
        vo.setId(mycouponVo.getId());
        vo.setShopId(mycouponVo.getShopId());
        if(mycouponVo.getStatus().intValue()==0){
            vo.setReceivedStatus(0);
            vo.setStatus(0);
            //通用券
            if(mycouponVo.getSurplus().intValue()==0){
                vo.setColor("#9F6DFA");
                vo.setLtBg("#FFFFFF");
                vo.setHeight("180rpx");
                vo.setUnit("￥");
                vo.setNumber(String.valueOf(mycouponVo.getDiscount().intValue()));
                vo.setMin(String.valueOf(mycouponVo.getMin().intValue()));
                vo.setTxt("满"+(mycouponVo.getMin().intValue())+"可用");
                vo.setTitle(mycouponVo.getTitle());
                vo.setDesc("有效期"+DateUtil.formatDate(mycouponVo.getEndTime()));
                vo.setBtn("去使用");
                vo.setDrawed("");
            }else{
                //专用券
                vo.setColor("#FF8830");
                vo.setLtBg("#FFFFFF");
                vo.setHeight("180rpx");
                vo.setUnit("￥");
                vo.setNumber(String.valueOf(mycouponVo.getDiscount().intValue()));
                vo.setMin(String.valueOf(mycouponVo.getMin().intValue()));
                vo.setTxt("满"+(mycouponVo.getMin().intValue())+"可用");
                vo.setTitle(mycouponVo.getTitle());
                vo.setDesc("有效期"+DateUtil.formatDate(mycouponVo.getEndTime()));
                vo.setBtn("去使用");
                vo.setDrawed("");
            }
        }else if(mycouponVo.getSurplus().intValue()==1){
            //已使用
            vo.setStatus(1);
            vo.setColor("#FF3456");
//            vo.setLtBg("#FFFFFF");
            vo.setLtBg("lightgrey");
            vo.setHeight("180rpx");
            vo.setUnit("￥");
            vo.setNumber(String.valueOf(mycouponVo.getDiscount().intValue()));
            vo.setMin(String.valueOf(mycouponVo.getMin().intValue()));
            vo.setTxt("满"+(mycouponVo.getMin().intValue())+"可用");
            vo.setTitle(mycouponVo.getTitle());
            vo.setDesc("有效期"+DateUtil.formatDate(mycouponVo.getEndTime()));
            vo.setBtn("已使用");
            vo.setDrawed("");
        }else if(mycouponVo.getSurplus().intValue()==2){
            //已过期
            vo.setStatus(2);
            vo.setColor("#696969");
//            vo.setLtBg("#FFFFFF");
            vo.setLtBg("lightgrey");
            vo.setHeight("180rpx");
            vo.setUnit("￥");
            vo.setNumber(String.valueOf(mycouponVo.getDiscount().intValue()));
            vo.setMin(String.valueOf(mycouponVo.getMin().intValue()));
            vo.setTxt("满"+(mycouponVo.getMin().intValue())+"可用");
            vo.setTitle(mycouponVo.getTitle());
            vo.setDesc("有效期"+DateUtil.formatDate(mycouponVo.getEndTime()));
            vo.setBtn("已过期");
            vo.setDrawed("");
        }
        return vo;
    }


    /**
     * 支付页面可用 优惠券列表
     * 状态：未使用、已使用、已过期
     * @param mycouponVo
     * @return
     */
    public static CouponVo getMyCouponvoForpay(MycouponVo mycouponVo){
        CouponVo vo = new CouponVo();
        vo.setId(mycouponVo.getId());
        vo.setShopId(mycouponVo.getShopId());
        if(mycouponVo.getStatus().intValue()==0){
            vo.setReceivedStatus(0);
            vo.setStatus(0);
            //通用券
            if(mycouponVo.getSurplus().intValue()==0){
                vo.setColor("#9F6DFA");
                vo.setLtBg("#FFFFFF");
                vo.setHeight("180rpx");
                vo.setUnit("￥");
                vo.setNumber(String.valueOf(mycouponVo.getDiscount().intValue()));
                vo.setMin(String.valueOf(mycouponVo.getMin().intValue()));
                vo.setTxt("满"+(mycouponVo.getMin().intValue())+"可用");
                vo.setTitle(mycouponVo.getTitle());
                vo.setDesc("有效期"+DateUtil.formatDate(mycouponVo.getEndTime()));
                vo.setBtn("使用");
                vo.setDrawed("");
            }else{
                //专用券
                vo.setColor("#FF8830");
                vo.setLtBg("#FFFFFF");
                vo.setHeight("180rpx");
                vo.setUnit("￥");
                vo.setNumber(String.valueOf(mycouponVo.getDiscount().intValue()));
                vo.setMin(String.valueOf(mycouponVo.getMin().intValue()));
                vo.setMin(String.valueOf(mycouponVo.getMin().intValue()));
                vo.setTxt("满"+(mycouponVo.getMin().intValue())+"可用");
                vo.setTitle(mycouponVo.getTitle());
                vo.setDesc("有效期"+DateUtil.formatDate(mycouponVo.getEndTime()));
                vo.setBtn("使用");
                vo.setDrawed("");
            }
            return vo;
        }
        return null;
    }
}
