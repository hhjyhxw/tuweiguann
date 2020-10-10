package com.icloud.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 店铺账号交易明细 
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Data
@TableName("shop_trade_details")
public class ShopTradeDetails implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /* 交易明细ID */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 所属店铺 */
//       @NotNull(message = "所属店铺不能为空")
       @TableField("shop_id")
       private Long shopId;
   	   	   /* 交易单号 */
       @TableField("trade_no")
       private String tradeNo;
   	   	   /* 对应单号 */
       @TableField("order_no")
       private String orderNo;
   	   	   /* 交易类型 10：订单收入，11：账号充值，20：账号提现，21：扣除订单手续费 */
       @TableField("biz_type")
       private Integer bizType;
   	   	   /* 收支方向 1：收入，2：支出 */
       @TableField("in_or_out")
       private Integer inOrOut;
   	   	   /* 变更前余额 */
       @TableField("before_blance")
       private BigDecimal beforeBlance;
   	   	   /* 变更金额 */
       @TableField("amount")
       private BigDecimal amount;
   	   	   /* 变更后余额 */
       @TableField("after_blance")
       private BigDecimal afterBlance;
   	   	   /* 创建人 */
       @TableField("created_by")
       private String createdBy;
   	   	   /* 创建时间 */
       @TableField("created_time")
       private Date createdTime;
   	   	   /* 更新人 */
       @TableField("updated_by")
       private String updatedBy;
   	   	   /* 更新时间 */
       @TableField("updated_time")
       private Date updatedTime;
       /* 店铺 */
       @TableField(exist = false)
       private Shop shop;
   	
}
