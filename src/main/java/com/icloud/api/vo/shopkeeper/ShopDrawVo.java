package com.icloud.api.vo.shopkeeper;

import com.icloud.common.validator.group.shop.SuSmallWasteGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现vo
 */
@Data
public class ShopDrawVo {
    /* 店铺id */
    @NotNull(message = "店铺不能为空",groups = SuSmallWasteGroup.class)
    private Long shopId;
    @NotNull(message = "银行卡不能为空",groups = SuSmallWasteGroup.class)
    private Long bankId;
    /* 金额 */
    @NotNull(message = "提现金额不能为空",groups = SuSmallWasteGroup.class)
    private BigDecimal amount;
}
