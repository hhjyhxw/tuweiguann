package com.icloud.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.api.vo.ShopAndOrderDetailVo;
import com.icloud.common.SnowflakeUtils;
import com.icloud.config.ServerConfig;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.entity.ShopTradeDetails;
import com.icloud.modules.small.entity.SmallOrder;
import com.icloud.modules.small.entity.SmallOrderDetail;
import com.icloud.modules.small.entity.SmallPurorder;
import com.icloud.modules.small.service.SmallOrderDetailService;
import com.icloud.modules.small.service.SmallOrderService;
import com.icloud.modules.small.service.SmallPurorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CreateShopTradeDetailsService{

    @Autowired
    private ShopService shopService;
    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private SmallOrderDetailService smallOrderDetailService;
    @Autowired
    private ShopTradeDetailsService shopTradeDetailsService;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private SmallPurorderService smallPurorderService;
    /**
     * 支付回调成功，添加零售户订单收入
     * 1、自营商品订单收入
     * 2、包含公共商品订单收入（自营部分 和 公共部分的佣金收入）
     * @param order
     */
    public void dealShopTradeDetails(SmallOrder order){
        //自营订单收入 = 实付金额
        BigDecimal totalAmout = new BigDecimal(0);
        Shop shop = (Shop) shopService.getById(order.getShopId());
        if(!"1".equals(order.getCommonFlag())){
            totalAmout = order.getActualPrice();
            ShopTradeDetails tradeDetails = new ShopTradeDetails();
            BigDecimal shopbalance = shop.getBalance();
            tradeDetails.setAmount(totalAmout);
            tradeDetails.setBeforeBlance(shopbalance!=null?shopbalance:new BigDecimal(0));
            tradeDetails.setAfterBlance(tradeDetails.getBeforeBlance().add(totalAmout));
            tradeDetails.setBizType(10);///* 交易类型 7、零售采购收入 8、佣金收入 9、公共订单收入（自营部分 10：自营订单收入，11：账号充值，20：账号提现，21：扣除订单手续费 */
            tradeDetails.setInOrOut(1);//1收入 2支出
            tradeDetails.setCreatedTime(new Date());
            tradeDetails.setTradeNo("B"+SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort()%31L));
            tradeDetails.setOrderNo(order.getOrderNo());
            tradeDetails.setShopId(shop.getId());
            shopTradeDetailsService.save(tradeDetails);
            Shop newshop = new Shop();
            newshop.setId(shop.getId());
            newshop.setBalance(shopbalance!=null?shopbalance.add(totalAmout):totalAmout);
            shopService.updateById(newshop);
            SmallOrder neworder = new SmallOrder();
            neworder.setId(order.getId());
            neworder.setDealStatus("1");
            smallOrderService.updateById(neworder);
        }else{
            //公共商品订单处理
            //1、自营收入部分处理
            Map<String,ShopAndOrderDetailVo> shopMap = new HashMap<String, ShopAndOrderDetailVo>();
            List<SmallOrderDetail> orderDetailList = smallOrderDetailService.list(new QueryWrapper<SmallOrderDetail>().eq("order_id",order.getId()));
            BigDecimal commissionAmout = new BigDecimal(0);
            for (int i = 0; i <orderDetailList.size() ; i++) {
                SmallOrderDetail orderDetail = orderDetailList.get(i);
                //自营商品
                if("0".equals(orderDetail.getCommonFlag())){
                    totalAmout = totalAmout.add(orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getNum())));
                }else{
                    commissionAmout = commissionAmout.add(orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getNum())));
                }
            }

            //保存自营商品金额
            ShopTradeDetails tradeDetails = new ShopTradeDetails();
            BigDecimal shopbalance = shop.getBalance();
            tradeDetails.setAmount(totalAmout);
            tradeDetails.setBeforeBlance(shopbalance!=null?shopbalance:new BigDecimal(0));
            tradeDetails.setAfterBlance(tradeDetails.getBeforeBlance().add(totalAmout));
            tradeDetails.setBizType(9);///* 交易类型 7、零售采购收入 8、佣金收入 9、公共订单收入（自营部分 10：自营订单收入，11：账号充值，20：账号提现，21：扣除订单手续费 */
            tradeDetails.setInOrOut(1);//1收入 2支出
            tradeDetails.setCreatedTime(new Date());
            tradeDetails.setOrderNo(order.getOrderNo());
            tradeDetails.setTradeNo("B"+SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort()%31L));
            tradeDetails.setShopId(shop.getId());
            shopTradeDetailsService.save(tradeDetails);
            Shop newshop = new Shop();
            newshop.setId(shop.getId());
            newshop.setBalance(shopbalance!=null?shopbalance.add(totalAmout):totalAmout);
            shopService.updateById(newshop);

            //保存公共商品佣金记录
            tradeDetails = new ShopTradeDetails();
            //计算公共商品佣金
            totalAmout = commissionAmout.multiply(shop.getCommissionRate());
            shop = (Shop) shopService.getById(order.getShopId());
            shopbalance = shop.getBalance();
            tradeDetails.setAmount(totalAmout);
            tradeDetails.setBeforeBlance(shopbalance!=null?shopbalance:new BigDecimal(0));
            tradeDetails.setAfterBlance(tradeDetails.getBeforeBlance().add(totalAmout));
            tradeDetails.setBizType(8);///* 交易类型 7、零售采购收入 8、佣金收入 9、公共订单收入（自营部分 10：自营订单收入，11：账号充值，20：账号提现，21：扣除订单手续费 */
            tradeDetails.setInOrOut(1);//1收入 2支出
            tradeDetails.setCreatedTime(new Date());
            tradeDetails.setOrderNo(order.getOrderNo());
            tradeDetails.setTradeNo("B"+SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort()%31L));
            tradeDetails.setShopId(shop.getId());
            shopTradeDetailsService.save(tradeDetails);

            newshop = new Shop();
            newshop.setId(shop.getId());
            newshop.setBalance(shopbalance!=null?shopbalance.add(totalAmout):totalAmout);
            shopService.updateById(newshop);


            //添加系统店铺公共商品售卖收入
            List<SmallPurorder> purorderlist = smallPurorderService.list(new QueryWrapper<SmallPurorder>().eq("order_id",order.getId()));
            dealpurOrdrerTradeDetails(purorderlist);

            SmallOrder neworder = new SmallOrder();
            neworder.setId(order.getId());
            neworder.setDealStatus("1");
            smallOrderService.updateById(neworder);
        }
    }

    /**
     * 公共商品（采购单收入）
     * @param purorderlist
     */
    public void dealpurOrdrerTradeDetails(List<SmallPurorder> purorderlist){
        for (int i=0;i<purorderlist.size();i++){
            SmallPurorder order = purorderlist.get(i);
            //系统店铺（被采购店铺）
            Shop sysshop = (Shop) shopService.getById(order.getSysShopId());
            //上架商品店铺（采购店铺）
            Shop commoshop = (Shop) shopService.getById(order.getSysShopId());
            BigDecimal shopbalance = sysshop.getBalance();
            ShopTradeDetails tradeDetails = new ShopTradeDetails();

            BigDecimal totalAmout = order.getActualPrice().subtract(commoshop.getCommissionRate().multiply(order.getActualPrice()));
            tradeDetails.setAmount(totalAmout);
            tradeDetails.setBeforeBlance(shopbalance!=null?shopbalance:new BigDecimal(0));
            tradeDetails.setAfterBlance(tradeDetails.getBeforeBlance().add(totalAmout));
            tradeDetails.setBizType(7);///* 交易类型 7、采购收入 8、佣金收入 9、公共订单收入（自营部分 10：自营订单收入，11：账号充值，20：账号提现，21：扣除订单手续费 */
            tradeDetails.setInOrOut(1);//1收入 2支出
            tradeDetails.setCreatedTime(new Date());
            tradeDetails.setOrderNo(order.getPurorderNo());
            tradeDetails.setTradeNo("B"+SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort()%31L));
            tradeDetails.setShopId(sysshop.getId());
            shopTradeDetailsService.save(tradeDetails);
            Shop newshop = new Shop();
            newshop.setId(sysshop.getId());
            newshop.setBalance(shopbalance!=null?shopbalance.add(totalAmout):totalAmout);
            shopService.updateById(newshop);
        }
    }

}
