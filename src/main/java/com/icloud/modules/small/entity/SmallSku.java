package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品sku
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Data
@TableName("t_small_sku")
public class SmallSku implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 商品spuid */
       @NotNull(message = "spuId不能为空")
       @TableField("spu_id")
       private Long spuId;
   	   	   /* sku条码 */
       @TableField("bar_code")
       private String barCode;
   	   	   /* sku名称 */
       @NotBlank(message = "规格名称不能为空")
       @TableField("title")
       private String title;
   	   	   /* 图片 */
       @TableField("img")
       private String img;
   	   	   /* 原始价 */
       @NotNull(message = "原价不能为空")
       @TableField("original_price")
       private BigDecimal originalPrice;
   	   	   /* 现价 */
       @NotNull(message = "现价不能为空")
       @TableField("price")
       private BigDecimal price;
   	   	   /* vip价 */

       @TableField("vip_price")
       private BigDecimal vipPrice;
   	   	   /* 库存 */
       @TableField("stock")
       private Integer stock;
   	   	   /* 冻结库存 */
       @TableField("freeze_stock")
       private Integer freezeStock;
   	   	   /* 创建时间 */
       @TableField("create_time")
       private Date createTime;
   	   	   /* 修改时间 */
       @TableField("modify_time")
       private Date modifyTime;


        /*增加的库存*/
        @TableField(exist = false)
        private Integer addStock;
        /*剩余库存*/
        @TableField(exist = false)
        private Integer remainStock;
   	
}
