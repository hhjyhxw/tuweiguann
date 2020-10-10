package com.icloud.api.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GroupSkuVo {
    /*  */
    private Long id;
    /* 关联商品sku_id */
    private Long skuId;
    /* 关联商品spu_id */
    private Long spuId;
    /* 团购价 */
    private BigDecimal minPrice;
    /* 单买价 */
    private BigDecimal maxPrice;
    /* 团购开始时间 */
    private Date gmtStart;
    /* 团购结束时间 */
    private Date gmtEnd;
    /* 团购基础人数 */
    private Integer minimumNumber;
    /* 团购已经购买人数 */
    private Integer alreadyBuyNumber;
    /* 判断团购商品是否在活动期间（0 停用 1使用） */
    private Integer status;
    /* 商户id */
    private Long shopId;


    private String title;
    /* 图片 */
    private String img;

}
