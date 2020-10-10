package com.icloud.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 店铺仓库 
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Data
@TableName("shop_store")
public class ShopStore implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /* 仓库ID */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 所属店铺 */
       @NotNull(message = "所属店铺不能为空")
       @TableField("shop_id")
       private Long shopId;
   	   	   /* 仓库名称 */
       @NotBlank(message = "仓库名称不能为空")
       @TableField("titile")
       private String titile;
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
   	   	   /* 联系电话 */
       @TableField("phone")
       private String phone;
   	   	   /* 经度 */
       @TableField("lnt")
       private BigDecimal lnt;
   	   	   /* 纬度 */
       @TableField("lat")
       private BigDecimal lat;
   	   	   /* 配送范围(米) */
       @TableField("distribution_scope")
       private Integer distributionScope;
   	   	   /* 状态 0：关闭，1：开启 */
       @TableField("status")
       private String status;
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
