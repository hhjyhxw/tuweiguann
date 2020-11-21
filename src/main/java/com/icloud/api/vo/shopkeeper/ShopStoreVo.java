package com.icloud.api.vo.shopkeeper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.icloud.modules.shop.entity.Shop;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺仓库 
 * 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Data
public class ShopStoreVo implements Serializable {

	private static final long serialVersionUID = 1L;

       private Long id;
       @NotNull(message = "店铺id不能为空")
       private Long shopId;
       @NotEmpty(message = "地址不能为空")
       private String address;
}
