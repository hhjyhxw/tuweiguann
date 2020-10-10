package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.wx.entity.WxUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户下单后 生成一条用户与店铺关联记录，如果存在则更新
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-28 09:14:58
 */
@Data
@TableName("t_small_shopconectuser")
public class SmallShopconectuser implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /*  */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 用户id */
       @TableField("user_id")
       private Long userId;
   	   	   /* 店铺id */
       @TableField("shop_id")
       private Long shopId;
        /* 店铺id */
        @TableField("dept_id")
        private Long deptId;
   	   	   /* 创建时间 */
       @TableField("create_time")
       private Date createTime;
   	   	   /* 修改时间 */
       @TableField("modify_time")
       private Date modifyTime;

        @TableField(exist = false)
        private WxUser user;

        @TableField(exist = false)
        private Shop shop;
   	
}
