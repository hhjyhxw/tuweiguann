package com.icloud.modules.small.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PresOrder {
    @NotNull(message = "商品skus为空")
    private Long[] skuId;//商品id
    @NotNull(message = "商品数量为空")
    private Long[] num;//商品数量
    private String types;// cart(购物车跳转)    buynow（商品详情页立即购买）
    @NotNull(message = "店铺id为空")
    private Long shopId;//商户id
    private String memo;//订单留言
    @NotNull(message = "支付方式为空")
    private String payType;//支付方式
}
