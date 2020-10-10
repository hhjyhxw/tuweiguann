package com.icloud.modules.shop.service;

import com.icloud.common.SpringContextHolder;
import com.icloud.modules.small.entity.SmallOrder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderTaskService implements Runnable{

    private SmallOrder smallOrder;
    private CreateShopTradeDetailsService createShopTradeDetailsService;
    public OrderTaskService(SmallOrder smallOrder){
        this.smallOrder = smallOrder;
        this.createShopTradeDetailsService = SpringContextHolder.getBean("createShopTradeDetailsService");
    }
    @Override
    public void run() {
        createShopTradeDetailsService.dealShopTradeDetails(smallOrder);
    }
}
