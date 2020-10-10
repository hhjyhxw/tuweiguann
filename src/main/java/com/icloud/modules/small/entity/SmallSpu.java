package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.icloud.modules.shop.entity.Shop;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品spu
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Data
@TableName("t_small_spu")
public class SmallSpu implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /*  现价*/
       @NotNull(message = "现价不能为空")
       @TableField("price")
       private BigDecimal price;
   	   	   /*  原价 */
        @NotNull(message = "原价不能为空")
        @TableField("original_price")
       private BigDecimal originalPrice;
   	   	   /* vip价 (团购价)*/

       @TableField("vip_price")
       private BigDecimal vipPrice;
   	   	   /* 商品名称 */
       @NotBlank(message = "商品名称")
       @TableField("title")
       private String title;
   	   	   /* 销量 */
       @TableField("sales")
       private Integer sales;
   	   	   /* 商品图片 */
       @NotBlank(message = "商品头图不能为空")
       @TableField("img")
       private String img;
   	   	   /* 商品详情 */
       @TableField("detail")
       private String detail;
   	   	   /* 商品描述 */
       @TableField("description")
       private String description;
   	   	   /* 分类id */
       @NotNull(message = "分类不能为空")
       @TableField("category_id")
       private Long categoryId;
   	   	   /* 运费模板id */
       @TableField("freight_template_id")
       private Long freightTemplateId;

   	    /* 计量单位 */
        @TableField("unit")
       private String unit;
   	   	   /* 0下架 1上架 */
       @TableField("status")
       private Integer status;
   	   	   /* 商户id\店铺Id*/
       @NotNull(message = "关联商户不能为空")
       @TableField("shop_id")
       private Long shopId;
   	   	   /* 热门 */
       @TableField("ihot")
       private Integer ihot;
   	   	   /* 新品 */
       @TableField("inew")
       private Integer inew;
   	   	   /* 折扣 */
       @TableField("idiscount")
       private Integer idiscount;
   	   	   /* 优选 */
       @TableField("iselect")
       private Integer iselect;
   	   	   /* 创建时间 */
       @TableField("create_time")
       private Date createTime;
   	   	   /* 修改时间 */
       @TableField("modify_time")
       private Date modifyTime;
        /* 库存 */
        @TableField("stock")
        private Integer stock;
        /* 冻结库存 */
        @TableField("freeze_stock")
        private Integer freezeStock;
        /* 店铺个性分类id */
        @TableField("sellcategory_id")
        private Long sellcategoryId;
        /*关联分类*/
        @TableField(exist = false)
        private SmallCategory smallCategory;
        /*消费商户*/
        @TableField(exist = false)
        private Shop shop;
        /*关联商户个性化分类*/
        @TableField(exist = false)
        private SmallSellCategory smallSellCategory;
        /*增加的库存*/
        @TableField(exist = false)
        private Integer addStock;
        /*剩余库存*/
        @TableField(exist = false)
        private Integer remainStock;
   	
}
