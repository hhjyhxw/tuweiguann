package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.modules.shop.entity.Shop;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 折扣券管理
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:57
 */
@Data
@TableName("t_small_coupon")
public class SmallCoupon implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 代金券名称 */
       @NotBlank(message = "优惠券名称不能为空")
       @TableField("title")
       private String title;
   	   	   /* 使用类型，如满减 1满减类型 2*/
       @TableField("coup_type")
       private Integer coupType;
   	   	   /* 描述 */
       @TableField("description")
       private String description;
   	   	   /* 发行总数 */
       @NotNull(message = "投放量不能为空")
       @TableField("total")
       private Integer total;
        @TableField("freeze_store")
        private Integer freezeStore;
   	   	   /* 使用类型0:默认满足分类可用 1新用户专用 */
       @TableField("surplus")
       private Integer surplus;
   	   	   /* 每人限领多少张 */
       @NotNull(message = "人限领多少张不能为空")
       @TableField("limits")
       private Integer limits;
   	   	   /* 减少金额 */
       @NotNull(message = "优惠金额不能为空")
       @TableField("discount")
       private BigDecimal discount;
   	   	   /* 满多少金额 */
       @NotNull(message = "最低消费金额不能为空")
       @TableField("min")
       private BigDecimal min;
   	   	   /* 是否可用 0不用 1可用 */
       @TableField("status")
       private Integer status;
   	   	   /* 分类id(分类可用) */
       @TableField("category_id")
       private Long categoryId;
        /* 有效期类型（0 领取后开始有效天数，1设置自定义有效期范围） */
       @TableField("validate_type")
       private Integer validateType;
   	   	   /* 过期天数  新用户领取后多少天有效*/
       @TableField("days")
       private Integer days;
   	   	   /* 领取开始时间 */
       @TableField("start_time")
       private Date startTime;
   	   	   /* 领取/使用结束时间 */
       @TableField("end_time")
       private Date endTime;
   	   	   /* 创建时间 */
       @TableField("create_time")
       private Date createTime;
   	   	   /* 修改时间 */
       @TableField("modify_time")
       private Date modifyTime;
   	   	   /* 商户id */
       @NotNull(message = "店铺不能为空")
       @TableField("shop_id")
       private Long shopId;
        /* 排序 */
        @TableField("order_num")
        private Integer orderNum;

    /* 是否已领取 */
    @TableField(exist = false)
    private Integer receivedStatus;

    /* 优惠券有效开始时间（领取优惠券时写入） */
    @TableField(exist = false)
    private String startTimeStr;
    /* 优惠券有效结束时间（领取优惠券过期时间根据策略计算） */
    @TableField(exist = false)
    private String endTimeStr;

    /*关联分类*/
    @TableField(exist = false)
    private SmallCategory smallCategory;
    /*消费商户*/
    @TableField(exist = false)
    private Shop shop;
   	
}
