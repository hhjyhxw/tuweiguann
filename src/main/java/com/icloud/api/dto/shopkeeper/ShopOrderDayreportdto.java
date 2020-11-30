package com.icloud.api.dto.shopkeeper;

import lombok.Data;

@Data
public class ShopOrderDayreportdto {

    private String direct; //方向 -1 向后 1 先前 0 查询当前输入多日期
    private String datetime;//查询月份 yyyy-MM
    private String formatdatetime; //2020年10月
    private Integer pageNum;
    private Integer pageSize;


}
