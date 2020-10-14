package com.icloud.api.vo;

import lombok.Data;


@Data
public class CouponVo {
    private Long id;
    private Long shopId;
    private String color;//颜色
    private String ltBg;//背景
    private String height;//高度
    private String unit;//单位
    private String number;//多少折扣
    private String txt;//满50元可用
    private String title;//标题
    private String desc;//有效期至 2018-05-20
    private String btn;//标题
    private String drawed;//已抢2100张
    /* 是否已领取 0未领取，1已领取*/
    private Integer receivedStatus;


}
