package com.icloud.modules.small.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ShopTreeVo {
    private Long id;
    private Long parentId;
    private String name;
    private String parentName;
    /**
     * ztree属性
     */
    private Boolean open;

}
