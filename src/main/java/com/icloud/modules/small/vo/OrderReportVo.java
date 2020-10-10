package com.icloud.modules.small.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderReportVo {
    private BigDecimal orderAmout;//订单总金额
    private Integer orderCount;//订单总数
    private String shopName;//店铺名称
    private String createTime;
}
