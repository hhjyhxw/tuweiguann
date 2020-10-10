package com.icloud.modules.small.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 团购商品
 */
@Data
public class GroupSkuVo {

    private Long id;
    /* 商品skuId */
    private Long skuId;
    /* 商品spuid */
    private Long spuId;
    /* sku名称 */
    private String title;
    /* 图片 */
    private String img;
    /* 原始价 */
    private BigDecimal originalPrice;
    /* 现价 */
    private BigDecimal price;
    /* 剩余库存 */
    private Integer remainStock;

    private Long shopId;

    private String shopName;
}
