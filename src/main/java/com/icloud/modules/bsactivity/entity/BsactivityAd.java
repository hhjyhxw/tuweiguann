package com.icloud.modules.bsactivity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.modules.shop.entity.Shop;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-04-26 16:19:44
 */
@Data
@TableName("t_bsactivity_ad")
public class BsactivityAd implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 名称 */
       @NotEmpty(message = "广告名称不能为空")
       @TableField("ad_name")
       private String adName;
   	   	   /* 图片url */
       @NotEmpty(message = "广告图片不能为空")
       @TableField("ad_imgurl")
       private String adImgurl;
   	   	   /* 跳转url */
       @TableField("add_jumpurl")
       private String addJumpurl;
   	   	   /* 0停用、1启用 */
       @TableField("status")
       private Integer status;
   	   	   /* 创建时间 */
       @TableField("create_time")
       private Date createTime;
   	   	   /* 创建人 */
       @TableField("create_operator")
       private String createOperator;
   	   	   /* 修改时间 */
       @TableField("modify_time")
       private Date modifyTime;
   	   	   /* 修改人 */
       @TableField("modify_operator")
       private String modifyOperator;
   	   	   /* 所在广告位 */
       @TableField("posittion_id")
       private Long posittionId;
   	   	   /* 排序 */
       @TableField("sort_num")
       private Integer sortNum;
        /* 店铺id */
        @NotNull(message = "店铺id不能为空")
        @TableField("shop_id")
        private Long shopId;
        /* 是否是系统广告 */
        @TableField("sys_flag")
        private String sysFlag;
        /* 所属商户 */
        @TableField(exist = false)
        private Shop shop;
   	
}
