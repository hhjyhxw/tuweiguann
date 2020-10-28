package com.icloud.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.modules.sys.entity.SysUserEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 店铺 
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Data
@TableName("shop")
public class Shop implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /* 店铺ID */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 上级店铺ID */
       @NotNull(message = "上级不能为空")
       @TableField("parent_id")
       private Long parentId;
   	   	   /* 编码 */
       @TableField("shop_code")
       private String shopCode;
   	   	   /* 名称 */
       @NotBlank(message = "店铺名称不能为空")
       @TableField("shop_name")
       private String shopName;
   	   	   /* 系统店铺标志 */
       @NotNull(message = "系统标识不能为空")
       @TableField("sys_flag")
       private String sysFlag;
   	   	   /* 级别 */
       @TableField("shop_level")
       private Integer shopLevel;
   	   	   /* 电话 */
       @TableField("shop_tel")
       private String shopTel;
   	   	   /* 图片 */
       @NotBlank(message = "店铺图标不能为空")
       @TableField("shop_img")
       private String shopImg;
   	   	   /* 简介 */
       @TableField("description")
       private String description;
   	   	   /* 余额 */
       @TableField("balance")
       private BigDecimal balance;
   	   	   /* 店铺地址 */
       @TableField("untitled4")
       private String untitled4;
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
   	   	   /* 经度 */
       @TableField("lnt")
       private BigDecimal lnt;
   	   	   /* 纬度 */
       @TableField("lat")
       private BigDecimal lat;
   	   	   /* 覆盖范围(米) */
       @TableField("cover_scope")
       private Integer coverScope;
   	   	   /* 状态 0：关闭，1：开启 */
       @TableField("status")
       private String status;
           /* 审核状态0未审核 1提交审核 2审核通过 3审核不通过 */
       @TableField("review")
       private String review;
        @TableField("msg")
        private String msg;
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
        /* 绑定用户id */
        @TableField("user_id")
        private Long userId;
        /*佣金率*/
        @TableField("commission_rate")
        private BigDecimal commissionRate;

        /**
         * 上级名称
         */
        @TableField(exist=false)
        private String parentName;
        /**
         * 当前激活的店铺
         */
        @TableField(exist=false)
        private boolean active;

        @TableField(exist=false)
        private SysUserEntity user;

}
