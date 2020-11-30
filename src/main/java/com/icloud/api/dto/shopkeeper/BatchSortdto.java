package com.icloud.api.dto.shopkeeper;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 批量更新排序
 */
@Data
public class BatchSortdto {
    @NotNull(message = "ids不能为空")
    private Long[] ids;
    @NotNull(message = "排序不能为空")
    private Integer[] sortNum;
}
