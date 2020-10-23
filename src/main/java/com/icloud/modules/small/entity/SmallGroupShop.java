package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.modules.shop.entity.Shop;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 团购商品
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-18 09:08:35
 */
@Data
@TableName("t_small_group_shop")
public class SmallGroupShop implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
     /* 关联商品sku_id */
      @NotNull(message = "请选择商品")
      @TableField("sku_id")
      private Long skuId;
   	   	   /* 关联商品spu_id */
       @NotNull(message = "请选择商品")
       @TableField("spu_id")
       private Long spuId;
   	   	   /* 团购价 */
       @NotNull(message = "团购价不能为空")
       @TableField("min_price")
       private BigDecimal minPrice;
   	   	   /* 单买价 */
       @NotNull(message = "单买价不能为空")
       @TableField("max_price")
       private BigDecimal maxPrice;
   	   	   /* 团购开始时间 */
//       @NotNull(message = "团购开始时间不能为空")
       @TableField("gmt_start")
       private Date gmtStart;
   	   	   /* 团购结束时间 */
//       @NotNull(message = "团购结束时间不能为空")
       @TableField("gmt_end")
       private Date gmtEnd;
   	   	   /* 团购基础人数 */
//       @NotNull(message = "团购基础人数不能为空")
       @TableField("minimum_number")
       private Integer minimumNumber;
   	   	   /* 团购已经购买人数 */
       @TableField("already_buy_number")
       private Integer alreadyBuyNumber;
   	   	   /* 团购结束时购买人数未达到基础人数,是否自动退款（0 不自动退 1自动退款） */
       @TableField("automatic_refund")
       private Integer automaticRefund;
   	   	   /* 判断团购商品是否在活动期间（0 停用 1使用） */
       @TableField("status")
       private Integer status;
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

       /*公共商品所属店铺Id (非系统店铺上架系统店铺商品 存储系统店铺id，上架自己店铺商品存储 店铺本身id)*/
        @TableField("sys_shop_id")
        private Long sysShopId;
        /* 是否是公共商品0 不是 1是*/
        @TableField("common_flag")
        private String commonFlag;
        /*关联分类*/
        @TableField(exist = false)
        private SmallCategory smallCategory;
        /* 所属商户 */
        @TableField(exist = false)
        private Shop shop;
        /* 公共商品所在店铺 */
        @TableField(exist = false)
        private Shop sysShop;
        /* 剩余库存 */
//        @TableField(exist = false)
//        private Integer remainStock;

        @TableField(exist = false)
        private SmallSpu spu;
        @TableField(exist = false)
        private SmallSku sku;


   	
}
