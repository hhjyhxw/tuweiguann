package com.icloud.api.dto.shopkeeper;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 绑定成为店主vo
 */
@Data
public class ShopkeeperBinddto {

    @NotEmpty(message = "账号不能为空")
    private String accountNo;
    /* 登录密码 */
    @NotEmpty(message = "密码不能为空")
    private String pwd;
    @NotNull(message = "店铺id不能为空")
    private Long shopId;
}
