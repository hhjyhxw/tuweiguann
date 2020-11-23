package com.icloud.api.vo.shopkeeper;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 批量操作状态
 */
@Data
public class BatchStatusVo {
    @NotNull(message = "ids不能为空")
    private Long[] ids;
    @NotEmpty(message = "状态不能为空")
    private String status;
}
