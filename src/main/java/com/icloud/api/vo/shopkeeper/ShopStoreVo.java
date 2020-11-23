package com.icloud.api.vo.shopkeeper;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 店铺自提地址vo
 */
@Data
public class ShopStoreVo implements Serializable {

	private static final long serialVersionUID = 1L;

       private Long id;//自提地址id
       @NotNull(message = "店铺id不能为空")
       private Long shopId;
       @NotEmpty(message = "地址不能为空")
       private String address;
}
