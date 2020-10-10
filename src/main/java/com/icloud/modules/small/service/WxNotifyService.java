package com.icloud.modules.small.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.icloud.common.DateUtil;
import com.icloud.common.PayUtil;
import com.icloud.config.threadpool.ThreadPoodExecuteService;
import com.icloud.modules.shop.service.CreateShopTradeDetailsService;
import com.icloud.modules.shop.service.OrderTaskService;
import com.icloud.modules.small.entity.SmallOrder;
import com.icloud.modules.small.entity.SmallOrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class WxNotifyService {

    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private SmallOrderDetailService mallOrderDetailService;
    @Autowired
    private SmallGroupShopService smallGroupShopService;
    @Autowired
    private SmallSpuService smallSpuService;

    public Object wxpaynotify(WxPayOrderNotifyResult result) {
        String orderNo = result.getOutTradeNo();
        String payId = result.getTransactionId();

        List<SmallOrder> orderList = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("order_no", orderNo));
        if (CollectionUtils.isEmpty(orderList)) {
            return WxPayNotifyResponse.fail("订单不存在 orderNo=" + orderNo);
        }
        SmallOrder order = orderList.get(0);
        // 检查这个订单是否已经处理过
        if (order.getPayStatus().intValue()==2) {
            return WxPayNotifyResponse.success("订单已经处理成功!");
        }
        Integer totalFee = result.getTotalFee();
        // 检查支付订单金额
        if (totalFee.intValue()!=(PayUtil.getFinalmoneyInt(String.valueOf(order.getActualPrice())))) {
            return WxPayNotifyResponse.fail(order.getOrderNo() + " : 支付金额不符合 totalFee=" + totalFee);
        }
        //**************** 在此之前都没有 数据库修改 操作 所以前面是直接返回错误的 **********************//

        SmallOrder updateOrderDO = new SmallOrder();
        updateOrderDO.setPayId(payId);
        updateOrderDO.setPayChannel("WX");
        updateOrderDO.setPayPrice(PayUtil.fenTransYuan(result.getTotalFee()));
        updateOrderDO.setPayTime(DateUtil.parseTimeString(result.getTimeEnd(),"yyyyMMddHHmmss"));
        updateOrderDO.setModifyTime(new Date());
        updateOrderDO.setPayStatus(2);//已支付
        updateOrderDO.setOrderStatus(1);//处理中，未配送

        smallOrderService.changeOrderStatus(orderNo, 0,0, updateOrderDO);
        List<SmallOrderDetail> orderSkuDOList = mallOrderDetailService.list(
                new QueryWrapper<SmallOrderDetail>()
                        .eq("order_no", orderNo));
        orderSkuDOList.forEach(item -> {
            //增加销量
            smallSpuService.incSales(item.getSpuId(), item.getNum());
        });
        //异步处理资金流水
        ThreadPoodExecuteService.getTaskExecutor().execute(new OrderTaskService(order));
        return WxPayNotifyResponse.success("支付成功");
    }
}
