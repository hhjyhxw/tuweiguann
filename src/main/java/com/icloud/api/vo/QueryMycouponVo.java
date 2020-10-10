package com.icloud.api.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryMycouponVo {
    @NotNull(message = "groupId不能为空")
    private Long[] groupId;//商品id
    @NotNull(message = "商品skus不能为空")
    private Long[] skuId;//商品id
    @NotNull(message = "店铺id不能为空")
    private Long shopId;//商户id
    private Long userId;//用户id
}
