package com.icloud.modules.small.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderReportVo {
    private BigDecimal orderAmout;//订单总金额
    private BigDecimal couponPrice;//优惠金额
    private BigDecimal actualPrice;//订单应付金额
    private BigDecimal payPrice;//实际支付金额
    private Integer orderCount;//订单总数
    private String shopName;//店铺名称
    private Long shopId;//店铺名称
    private String createTime;//yyyy-MM
    private String querydate;//yyyy-MM-dd
}
