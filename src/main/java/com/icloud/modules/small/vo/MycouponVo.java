package com.icloud.modules.small.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MycouponVo {
    /*  */
    private Long id;
    private Long userId;
    /* 代金券id */
    private Long couponId;
    /* 使用订单Id */
    private Long orderId;
    /* 使用时间，若使用时间为空，表示未使用 */
    private Date useTime;
    /* 优惠券有效开始时间（领取优惠券时写入） */
    private Date startTime;
    /* 优惠券有效结束时间（领取优惠券过期时间根据策略计算） */
    private Date endTime;
    /* 优惠券有效开始时间（领取优惠券时写入） */
    private String startTimeStr;
    /* 优惠券有效结束时间（领取优惠券过期时间根据策略计算） */
    private String endTimeStr;
    /* 创建时间 */
    private Date createTime;

    private Long shopId;


    /* 代金券名称 */
    private String title;
    /* 描述 */
    private String description;
    /* 使用类型0:默认满足分类可用 1新用户专用 */
    private Integer surplus;
    /* 减少金额 */
    private BigDecimal discount;
    /* 满多少金额 */
    private BigDecimal min;

    /* 分类id(分类可用) */
    private Long categoryId;
    /* status:0 未领取 1已领取 2已过期 */
    private Integer status;

}
