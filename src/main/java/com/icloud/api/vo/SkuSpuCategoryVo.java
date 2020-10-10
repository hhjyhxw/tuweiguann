package com.icloud.api.vo;

import lombok.Data;

@Data
public class SkuSpuCategoryVo {

    private Long id;
    private Long spuId;
    private String title;
    private Long categoryId;
    private String categoryName;
}
