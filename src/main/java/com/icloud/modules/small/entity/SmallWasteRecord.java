package com.icloud.modules.small.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.common.validator.group.AddGroup;
import com.icloud.common.validator.group.shop.SuSmallWasteGroup;
import com.icloud.modules.shop.entity.Shop;
import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;


/**
 * 资金流水记录表
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-07 20:18:12
 */
@Data
@TableName("t_small_waste_record")
public class SmallWasteRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /*  */
    @TableId(value="id", type= IdType.AUTO)
    private Long id;
    /* 店铺id */
    @NotNull(message = "店铺不能为空",groups = SuSmallWasteGroup.class)
    @TableField("shop_id")
    private Long shopId;
    /* 支付方式1微信 2支付包 3银行卡 */
    @TableField("pay_type")
    private String payType;
    /* 操作类型1充值 2提现 */
    @TableField("waste_flag")
    private String wasteFlag;
    /* 金额 */
    @NotNull(message = "提现金额不能为空",groups = SuSmallWasteGroup.class)
    @TableField("amount")
    private BigDecimal amount;
    /* 创建时间 */
    @TableField("create_time")
    private Date createTime;
    /* 审批状态0未审核 1通过 2不通过 */
    @TableField("approve_flag")
    private String approveFlag;
    /* 支付状态0未支付 1已支付 2关闭 */
    @TableField("waste_state")
    private String wasteState;
    /* 第三方交易号 */
    @TableField("transaction_id")
    private String transactionId;
    /* 本地订单号 */
    @TableField("order_no")
    private String orderNo;
    /* 审核描述 */
    @TableField("msg")
    private String msg;
    /* 审核时间 */
    @TableField("approve_time")
    private Date approveTime;
    /* 修改时间 */
    @TableField("modify_time")
    private Date modifyTime;
    /* 审核人 */
    @TableField("approve_by")
    private String approveBy;
    /* 申请人 */
    @TableField("create_by")
    private String createBy;
    /*  */
    @TableField(exist = false)
    private Shop shop;

}
