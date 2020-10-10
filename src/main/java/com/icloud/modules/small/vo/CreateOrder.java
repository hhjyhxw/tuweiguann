package com.icloud.modules.small.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateOrder {
    @NotNull(message = "团购商品id不能为空")
    private Long[] groupId;//团购商品ids
    @NotNull(message = "商品skus不能为空")
    private Long[] skuId;//商品id
    @NotNull(message = "商品数量不能为空")
    private Long[] num;//商品数量
    private String types;// cart(购物车跳转)    buynow（商品详情页立即购买）
    @NotNull(message = "店铺id不能为空")
    private Long shopId;//商户id
    private Long addressId;//地址id
    private Long mycouponId;//我的优惠券id

    private String memo;//订单留言
    @NotNull(message = "支付方式不能为空")
    private String payType;//支付方式
}
