package com.icloud.api.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserAccount {
    @NotBlank
    private String account;
    @NotBlank
    private String password;
}
