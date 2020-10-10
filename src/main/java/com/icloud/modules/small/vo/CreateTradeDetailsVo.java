package com.icloud.modules.small.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateTradeDetailsVo {
    @NotNull(message = "店铺id不能为空")
    private Long shopId;//店铺id
    @NotNull(message = "操作金额不能为空")
    private BigDecimal amount;//操作金额
    @NotNull(message = "交易类型不能为空")
    private Integer bizType;//* 交易类型 7、零售采购收入 8、佣金收入 9、公共订单收入（自营部分 10：自营订单收入，11：账号充值，20：账号提现，21：扣除订单手续费 */
    @NotNull(message = "收支方向不能为空")
    private Integer inOrOut; /* 收支方向 1：收入，2：支出 */
    private String orderNo;//管理订单号
    private String updateBy;//
    private Long deptId;

}
