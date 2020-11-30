package com.icloud.api.dto.shopkeeper;

import lombok.Data;

import java.util.Date;

@Data
public class ShopGoodsDayreportdto {

    private String direct; //方向 -1 向后 1 先前 0 查询当前输入多日期
    private String datetime;//查询日期 yyyy-MM-dd
    private String formatdatetime; //2020年10月1日
    private Integer pageNum;
    private Integer pageSize;


}
