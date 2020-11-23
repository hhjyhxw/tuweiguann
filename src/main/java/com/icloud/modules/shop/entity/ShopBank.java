package com.icloud.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 店铺银行卡 用于店铺提现
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Data
@TableName("shop_bank")
public class ShopBank implements Serializable {
	private static final long serialVersionUID = 1L;

   	   /* 银行卡ID */
       @TableId(value="id", type= IdType.AUTO)
       private Long id;
   	   	   /* 所属店铺 */
//       @NotNull(message = "所属店铺不能为空")
       @TableField("shop_id")
       private Long shopId;
   	   	   /* 银行名称 */
       @NotBlank(message = "银行名称不能为空")
       @TableField("bank_name")
       private String bankName;
   	   	   /* 支行名称 */
       @TableField("sub_branch")
       private String subBranch;
   	   	   /* 银行卡号 */
       @NotBlank(message = "银行卡号不能为空")
       @TableField("card_no")
       private String cardNo;
   	   	   /* 用户姓名 */
       @NotBlank(message = "用户名不能为空")
       @TableField("user_name")
       private String userName;
   	   	   /* 手机号 */
       @NotBlank(message = "用户手机号不能为空")
       @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
       @TableField("mobile")
       private String mobile;
   	   	   /* 状态 0：禁用，1：正常 */
       @NotNull(message = "用户状态不能为空")
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
        /* 银行卡所在开户行编号,详见银行编号列表 */
        @NotBlank(message = "银行卡开户行编号不能为空")
        @TableField("bank_code")
        private String bankCode;

        /* 审核状态0未审核 1提交审核 2审核通过 3审核不通过 */
        @TableField("approve_flag")
        private String approveFlag;
        /* 审核不通过描述 */
        @TableField("msg")
        private String msg;

        /* 店铺 */
        @TableField(exist = false)
        private Shop shop;
   	
}
