package com.icloud.api.vo.shopkeeper;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
public class ShopCategoryVo {
    /*  */
    private Long id;
    /* 分类名称 */
    @NotEmpty(message = "分类名称不能为空")
    private String title;
    /* 父类id */
    private Long parentId;
    /* 分类图标地址 */
    @NotEmpty(message = "分类图标不能为空")
    private String picUrl;
    /* 分类级别 */
    private Integer level;
    /* 状态 */
    private Integer status;
    /* 创建时间 */
    private Date createTime;

    private Integer sortNum;
    /* 店铺id */
    private Long shopId;
    /*子分类*/
    private List<ShopCategoryVo> childList;
}
