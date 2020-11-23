package com.icloud.api.vo.shopkeeper;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 店铺vo
 */
@Data
public class ShopVo {

    @NotNull(message = "店铺id不能为空")
    private Long id;
    @NotEmpty(message = "状态不能为空")
    private String status;
}
