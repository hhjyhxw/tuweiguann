package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.small.vo.OrderDetailVo;
import com.icloud.modules.wx.entity.WxUser;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单表
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Data
@TableName("t_small_order")
public class SmallOrder implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 下单渠道 */
       @TableField("channel")
       private String channel;
   	   	   /* 订单号 */
       @TableField("order_no")
       private String orderNo;
        /* 订单类型 订单类型（0团购订单、1普通订单）*/
        @TableField("order_type")
        private String orderType;
   	   	   /* 用户id */
       @TableField("user_id")
       private Long userId;
   	   	   /* 店铺id */
       @TableField("shop_id")
       private Long shopId;
   	   	   /* 0生成、处理中、已完成 */
       @TableField("order_status")
       private Integer orderStatus;
   	   	   /* 0未支付、1支付中、2已支付 */
       @TableField("pay_status")
       private Integer payStatus;
   	   	   /* 0生成、1、退款中、2已退款 */
       @TableField("refund_status")
       private Integer refundStatus;
   	   	   /* 0未发货、1发货中、2已发货 */
       @TableField("ship_status")
       private Integer shipStatus;
   	   	   /* 商品(sku)原始价总额 */
       @TableField("sku_original_total_price")
       private BigDecimal skuOriginalTotalPrice;
   	   	   /* 商品(sku)现价总额 */
       @TableField("sku_total_price")
       private BigDecimal skuTotalPrice;
   	   	   /* 运费 */
       @TableField("freight_price")
       private BigDecimal freightPrice;
   	   	   /* 代金券优惠价 */
       @TableField("coupon_price")
       private BigDecimal couponPrice;
   	   	   /* 代金券id */
       @TableField("coupon_id")
       private Long couponId;
   	   	   /* 实付订单金额 */
       @TableField("actual_price")
       private BigDecimal actualPrice;
   	   	   /* 支付金额 */
       @TableField("pay_price")
       private BigDecimal payPrice;
   	   	   /* 支付流水id(本地支付流水) */
       @TableField("pay_id")
       private String payId;
   	   	   /* 支付渠道名称 */
       @TableField("pay_channel")
       private String payChannel;
   	   	   /* 支付时间(支付成功回调获取的支付时间) */
       @TableField("pay_time")
       private Date payTime;
   	   	   /* 物流方式(物流方式代号) */
       @TableField("ship_code")
       private String shipCode;
   	   	   /* 物流单号 */
       @TableField("ship_no")
       private String shipNo;
   	   	   /* 本地支付单号（用于与第三方支付交互） */
       @TableField("pay_sn")
       private String paySn;
   	   	   /* 发货时间 */
       @TableField("ship_time")
       private Date shipTime;
   	   	   /* 确认收货时间 */
       @TableField("confirm_time")
       private Date confirmTime;
   	   	   /* 省 */
       @TableField("province")
       private String province;
   	   	   /* 市 */
       @TableField("city")
       private String city;
   	   	   /* 县 */
       @TableField("county")
       private String county;
   	   	   /* 详细地址 */
       @TableField("address")
       private String address;
   	   	   /*  */
       @TableField("memo")
       private String memo;
   	   	   /*  */
       @TableField("refund_reason")
       private String refundReason;
   	   	   /*  */
       @TableField("consignee")
       private String consignee;
   	   	   /*  */
       @TableField("phone")
       private String phone;
   	   	   /*  */
       @TableField("create_time")
       private Date createTime;
   	   	   /*  */
       @TableField("modify_time")
       private Date modifyTime;
        /* 是否是公共商品订单0 不是 1是*/
        @TableField("common_flag")
        private String commonFlag;
        /* （0未生成了店铺订单收入流水，1生成了店铺订单收入流水）*/
        @TableField("deal_status")
        private String dealStatus;

       @TableField(exist = false)
       private List<OrderDetailVo> detaillist;
        /* 用于控制明细项是否隐藏或者显示 */
       @TableField(exist = false)
       private boolean show;

        /* 用户 */
        @TableField(exist = false)
        private WxUser user;
        /* 店铺 */
        @TableField(exist = false)
        private Shop shop;


}
