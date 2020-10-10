package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购单
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-06 20:50:58
 */
@Data
@TableName("t_small_purorder")
public class SmallPurorder implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 下单渠道 */
       @TableField("channel")
       private String channel;
   	   	   /* 采购单号 */
       @TableField("purorder_no")
       private String purorderNo;
   	   	   /* 关联订单id */
       @TableField("order_id")
       private Long orderId;
   	   	   /* 采购店铺id */
       @TableField("shop_id")
       private Long shopId;
        /*公共商品所属店铺Id*/
        @TableField("sys_shop_id")
        private Long sysShopId;
   	   	   /* 采购单类型(0公共商品售卖产生 1其他渠道采购) */
       @TableField("purorder_type")
       private Integer purorderType;
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
   	   	   /* 实付订单金额 */
       @TableField("actual_price")
       private BigDecimal actualPrice;
   	   	   /* 支付金额 */
       @TableField("pay_price")
       private BigDecimal payPrice;
   	   	   /* 支付流水id(本地支付流水) */
       @TableField("pay_id")
       private Long payId;
   	   	   /* 支付渠道名称 */
       @TableField("pay_channel")
       private String payChannel;
   	   	   /* 支付时间(支付成功回调获取的支付时间) */
       @TableField("pay_time")
       private Date payTime;
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
   	
}
