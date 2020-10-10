package com.icloud.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.small.entity.SmallOrderDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShopAndOrderDetailVo {
    private List<SmallOrderDetail> orderDetailList = new ArrayList<SmallOrderDetail>();
    private Shop shop;
    private BigDecimal skuOriginalTotalPrice;
    /* 商品(sku)现价总额 */
    private BigDecimal skuTotalPrice;
    /* 运费 */
    private BigDecimal freightPrice;
    /* 实付订单金额 */
    private BigDecimal actualPrice;
}
