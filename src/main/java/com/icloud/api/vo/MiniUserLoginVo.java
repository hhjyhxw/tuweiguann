package com.icloud.api.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 小程序登录相关参数
 */
@Data
public class MiniUserLoginVo {
    @NotBlank(message="appid不能为空")
    private String appid;
    @NotBlank(message="code不能为空")
    private String code;
    @NotBlank(message="encryptedData不能为空")
    private String encryptedData;
    @NotBlank(message="rawData不能为空")
    private String rawData;
    @NotBlank(message="signature不能为空")
    private String signature;
    @NotBlank(message="iv不能为空")
    private String iv;

}
