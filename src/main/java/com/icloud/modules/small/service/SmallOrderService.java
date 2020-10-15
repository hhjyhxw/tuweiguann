package com.icloud.modules.small.service;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.api.vo.ShopAndOrderDetailVo;
import com.icloud.basecommon.model.Query;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.basecommon.service.LockComponent;
import com.icloud.basecommon.service.redislock.DistributedLock;
import com.icloud.basecommon.service.redislock.DistributedLockUtil;
import com.icloud.common.*;
import com.icloud.config.ServerConfig;
import com.icloud.exceptions.ApiException;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.small.dao.SmallOrderMapper;
import com.icloud.modules.small.entity.*;
import com.icloud.modules.small.vo.CreateOrder;
import com.icloud.modules.small.vo.OrderReportVo;
import com.icloud.modules.wx.entity.WxUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单表
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 */
@Slf4j
@Service
@Transactional
public class SmallOrderService extends BaseServiceImpl<SmallOrderMapper,SmallOrder> {

    private static final String ORDER_STATUS_LOCK = "ORDER_STATUS_LOCK_";

    //订单退款乐观锁
    public static final String ORDER_REFUND_LOCK = "ORDER_REFUND_LOCK_";

    @Autowired
    private SmallOrderMapper smallOrderMapper;
    @Autowired
    private SmallSkuService smallSkuService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallOrderDetailService smallOrderDetailService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private SmallGroupShopService smallGroupShopService;
    @Autowired
    private SmallPurorderService smallPurorderService;
    @Autowired
    private SmallPurorderDetailService smallPurorderDetailService;
    @Autowired
    private SmallUserCouponService smallUserCouponService;
    @Autowired
    private SmallShopconectuserService smallShopconectuserService;
    @Autowired
    private DistributedLockUtil distributedLockUtil;

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private LockComponent lockComponent;

    @Override
    public PageUtils<SmallOrder> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
//        try {
//            query =  MapEntryUtils.mapvalueToBeanValueAndBeanProperyToColum(query, SmallOrder.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        PageHelper.startPage(pageNo, pageSize);
        List<SmallOrder> list = smallOrderMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallOrder> pageInfo = new PageInfo<SmallOrder>(list);
        PageUtils<SmallOrder> page = new PageUtils<SmallOrder>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
    public R createOrder(CreateOrder preOrder, WxUser user, SmallAddress address,SmallUserCoupon userCoupon,SmallCoupon smallCoupon) {
        BigDecimal totalAmout = new BigDecimal(0);//订单总金额
        BigDecimal origintotalAmout = new BigDecimal(0);//原价总额
        int totalNum = 0;//总数量
        //1、库存校验
        for(int i=0;i<preOrder.getSkuId().length;i++){
            SmallGroupShop group = (SmallGroupShop) smallGroupShopService.getById(preOrder.getGroupId()[i]);
            SmallSku spu = (SmallSku) smallSkuService.getById(preOrder.getSkuId()[i]);
            if(group.getShopId().longValue()!=preOrder.getShopId().longValue()){
                return R.error("团购商品与商户对应不上");
            }

            //剩余库存
            int remainStock = spu.getStock()-(spu.getFreezeStock()!=null?spu.getFreezeStock():0);
            if(remainStock<=0 || remainStock<preOrder.getNum()[i]){
                return R.error(spu.getTitle()+" 库存不足");
            }
//            totalAmout+=spu.getPrice()*preOrder.getNum()[i];
//            totalAmout = totalAmout.add(spu.getPrice().multiply(new BigDecimal(preOrder.getNum()[i])).setScale(2,BigDecimal.ROUND_HALF_UP));
            totalAmout = totalAmout.add(group.getMinPrice().multiply(new BigDecimal(preOrder.getNum()[i])).setScale(2,BigDecimal.ROUND_HALF_UP));
            origintotalAmout = origintotalAmout.add(group.getMaxPrice().multiply(new BigDecimal(preOrder.getNum()[i])).setScale(2,BigDecimal.ROUND_HALF_UP));
            totalNum+=preOrder.getNum()[i];
        }
        //2、冻结库存
        for(int i=0;i<preOrder.getSkuId().length;i++){
            Long skuId = preOrder.getSkuId()[i];
            Integer num = preOrder.getNum()[i].intValue();
            DistributedLock lock = distributedLockUtil.getDistributedLock(skuId.toString());
            try {
                if (lock.acquire()) {
                    //获取锁成功业务代码
                    SmallSku spu = (SmallSku) smallSkuService.getById(skuId);
//                    SmallSpu spu = (SmallSpu) smallSpuService.getById(skuId);
                    spu.setFreezeStock(spu.getFreezeStock()!=null?spu.getFreezeStock()+num:num);
                    boolean result = smallSkuService.updateById(spu);
                    if(!result){
                        log.error("兑换时,更新商品库存失败");
                        throw new ApiException("更新商品库存失败");
                    }
                } else { // 获取锁失败
                    //获取锁失败业务代码
                    throw new ApiException("系统繁忙,请稍后再试");
                }
            } finally {
                if (lock != null) {
                    lock.release();
                }
            }
        }
        //生成订单
        SmallOrder order = new SmallOrder();
        order.setChannel("小程序商品购买");
        if(userCoupon!=null){
            order.setCouponId(userCoupon.getId());
            order.setCouponPrice(smallCoupon.getDiscount());
            order.setSkuTotalPrice(totalAmout);
            order.setActualPrice(totalAmout.subtract(smallCoupon.getDiscount()));
            order.setSkuOriginalTotalPrice(origintotalAmout);
            SmallUserCoupon newuserCoupon = new SmallUserCoupon();
            newuserCoupon.setId(userCoupon.getId());
            newuserCoupon.setStatus(1);
            smallUserCouponService.updateById(newuserCoupon);
        }else{
            order.setActualPrice(totalAmout);
            order.setSkuTotalPrice(totalAmout);
            order.setSkuOriginalTotalPrice(origintotalAmout);
        }

//        order.setAddress(address.getAddress());
//        order.setCounty(address.getCounty());
//        order.setCity(address.getCity());
//        order.setPhone(address.getPhone());
//        order.setProvince(address.getProvince());
//        order.setConsignee(address.getName());
        order.setUserId(user.getId().longValue());
        order.setOrderStatus(0);
        order.setCreateTime(new Date());
        order.setMemo(preOrder.getMemo());
        Shop shop = (Shop)shopService.getById(preOrder.getShopId());
        order.setShopId(preOrder.getShopId());
        order.setOrderType("0");
        order.setOrderNo(SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort()%31L));
        order.setOrderStatus(0);//
        order.setPayStatus(0);
        order.setPayChannel("微信支付");
        order.setRefundStatus(0);
        order.setFreightPrice(new BigDecimal(0));
        order.setCouponPrice(new BigDecimal(0));
        order.setShipStatus(0);
        smallOrderMapper.insert(order);

        String productInfo = "";
        List<SmallOrderDetail> orderDetailList = new ArrayList<>();
        for(int i=0;i<preOrder.getSkuId().length;i++){
//            SmallSpu spu = (SmallSpu) smallSpuService.getById(preOrder.getSkuId()[i]);
            SmallSku sku = (SmallSku) smallSkuService.getById(preOrder.getSkuId()[i]);
            SmallGroupShop group = (SmallGroupShop) smallGroupShopService.getById(preOrder.getGroupId()[i]);

            SmallSpu spus = (SmallSpu) smallSpuService.getById(sku.getSpuId());
            SmallOrderDetail detail = new SmallOrderDetail();
            detail.setCreateTime(new Date());
            detail.setNum(preOrder.getNum()[i].intValue());
            detail.setOrderNo(order.getOrderNo());
            detail.setOrderId(order.getId());
            detail.setGroupId(preOrder.getGroupId()[i]);
            detail.setSkuId(preOrder.getSkuId()[i]);
            detail.setSkuTitle(sku.getTitle());
            detail.setShopId(preOrder.getShopId());
            detail.setSpuImg(sku.getImg()!=null?sku.getImg(): spus.getImg());
            detail.setOriginalPrice(group.getMaxPrice());
            detail.setPrice(group.getMinPrice());
            productInfo+=sku.getTitle()+"x"+detail.getNum()+";";
//            detail
            if("1".equals(group.getCommonFlag())){
                order.setCommonFlag("1");
                detail.setSysShopId(group.getSysShopId());
                detail.setCommonFlag("1");
            }else{
                detail.setCommonFlag("0");
                order.setCommonFlag("0");
            }
            smallOrderDetailService.save(detail);
            orderDetailList.add(detail);
        }
        smallOrderMapper.updateById(order);
        //生成采购单
        if("1".equals(order.getCommonFlag())){
            createPurorder(orderDetailList,order);
        }
        //生成店铺与会员记录
        List<SmallShopconectuser> userlist =smallShopconectuserService.list(new QueryWrapper<SmallShopconectuser>()
                .eq("shop_id",order.getShopId())
                .eq("user_id",order.getUserId()));
        if(userlist==null || userlist.size()==0){
            SmallShopconectuser couser = new SmallShopconectuser();
            couser.setShopId(order.getShopId());
            couser.setUserId(order.getUserId());
            couser.setCreateTime(new Date());
            smallShopconectuserService.save(couser);
        }
        //清空对应的购物车
        for(int i=0;i<preOrder.getSkuId().length;i++){
            smallCartService.remove(new QueryWrapper<SmallCart>()
                    .eq("user_id",user.getId())
                    .eq("group_id",preOrder.getGroupId()[i])
                    .eq("sku_id",preOrder.getSkuId()[i])
                    .eq("shop_id",preOrder.getShopId()));

        }
//        Shop shop = (Shop) shopService.getById(preOrder.getSupplierId());

        //生成发送通知消息任务，设置需要发送的消息
        //SmallPlaceOrderNotifyService smallPlaceOrderNotifyService = new SmallPlaceOrderNotifyService();
        //smallPlaceOrderNotifyService.setNotifyInof(order,user,shop,productInfo);
        //异步执行发送任务
        // ThreadPoodExecuteService.getTaskExecutor().execute(smallPlaceOrderNotifyService);

        return R.ok().put("orderNo",order.getOrderNo());
    }

    public void createPurorder(List<SmallOrderDetail> orderDetailList,SmallOrder order){
        //每个系统店铺 id 对应一个List<SmallOrderDetail>
        Map<String, ShopAndOrderDetailVo> shopMap = new HashMap<String, ShopAndOrderDetailVo>();
        for (int i = 0; i <orderDetailList.size() ; i++) {
            SmallOrderDetail orderDetail = orderDetailList.get(i);
            //自营商品
            if("1".equals(orderDetail.getCommonFlag())){
                ShopAndOrderDetailVo shopAndOrderDetailVo = shopMap.get(orderDetail.getSysShopId().toString());
                if(shopAndOrderDetailVo==null){
                    shopAndOrderDetailVo = new ShopAndOrderDetailVo();
                    shopAndOrderDetailVo.getOrderDetailList().add(orderDetail);
                    shopAndOrderDetailVo.setShop((Shop) shopService.getById(orderDetail.getSysShopId()));
                    shopAndOrderDetailVo.setSkuTotalPrice(orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getNum())));
                    shopAndOrderDetailVo.setSkuOriginalTotalPrice(orderDetail.getOriginalPrice().multiply(new BigDecimal(orderDetail.getNum())));
                    shopAndOrderDetailVo.setActualPrice(orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getNum())));
                }else{
                    shopAndOrderDetailVo.getOrderDetailList().add(orderDetail);
                    BigDecimal skuOriginalTotalAmout = orderDetail.getOriginalPrice().multiply(new BigDecimal(orderDetail.getNum()));
                    BigDecimal skuTotalAmout = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getNum()));
                    BigDecimal actualPriceAmout = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getNum()));

                    shopAndOrderDetailVo.setSkuOriginalTotalPrice(shopAndOrderDetailVo.getSkuOriginalTotalPrice().add(skuOriginalTotalAmout));
                    shopAndOrderDetailVo.setSkuTotalPrice(shopAndOrderDetailVo.getSkuTotalPrice().add(skuTotalAmout));
                    shopAndOrderDetailVo.setActualPrice(shopAndOrderDetailVo.getActualPrice().add(actualPriceAmout));
                }
                shopMap.put(orderDetail.getSysShopId().toString(),shopAndOrderDetailVo);
            }
        }
        List<ShopAndOrderDetailVo> list = new ArrayList<ShopAndOrderDetailVo>(shopMap.values());
        for (int i = 0; i <list.size() ; i++) {
            ShopAndOrderDetailVo vo = list.get(i);
            SmallPurorder smallPurorder = new SmallPurorder();
            BeanUtils.copyProperties(order,smallPurorder);
            smallPurorder.setId(null);
            smallPurorder.setSysShopId(vo.getShop().getId());
            smallPurorder.setOrderId(order.getId());
            smallPurorder.setSkuTotalPrice(vo.getSkuTotalPrice());
            smallPurorder.setSkuOriginalTotalPrice(vo.getSkuOriginalTotalPrice());
            smallPurorder.setActualPrice(vo.getActualPrice());//
            smallPurorder.setPurorderNo("P"+SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort()%31L));
            smallPurorder.setPurorderType(0);
            smallPurorderService.save(smallPurorder);
            for (int j = 0; j <vo.getOrderDetailList().size() ; j++) {
                SmallOrderDetail detail = vo.getOrderDetailList().get(j);
                SmallPurorderDetail pdetail = new SmallPurorderDetail();
                BeanUtils.copyProperties(detail,pdetail);

                pdetail.setId(null);
                pdetail.setPurorderId(smallPurorder.getId());
                pdetail.setPurorderNo(smallPurorder.getPurorderNo());
                smallPurorderDetailService.save(pdetail);
            }
        }
    }


























    public R createOrder_bak(CreateOrder preOrder, WxUser user, SmallAddress address) {
        BigDecimal totalAmout = new BigDecimal(0);//订单总金额
        BigDecimal origintotalAmout = new BigDecimal(0);//原价总额
        int totalNum = 0;//总数量
        //1、库存校验
        for(int i=0;i<preOrder.getSkuId().length;i++){
            SmallGroupShop group = (SmallGroupShop) smallGroupShopService.getById(preOrder.getGroupId()[i]);
            SmallSku spu = (SmallSku) smallSkuService.getById(preOrder.getSkuId()[i]);
            if(group.getShopId().longValue()!=preOrder.getShopId().longValue()){
                return R.error("团购商品与商户对应不上");
            }

            //剩余库存
            int remainStock = spu.getStock()-(spu.getFreezeStock()!=null?spu.getFreezeStock():0);
            if(remainStock<=0 || remainStock<preOrder.getNum()[i]){
                return R.error(spu.getTitle()+" 库存不足");
            }
//            totalAmout+=spu.getPrice()*preOrder.getNum()[i];
//            totalAmout = totalAmout.add(spu.getPrice().multiply(new BigDecimal(preOrder.getNum()[i])).setScale(2,BigDecimal.ROUND_HALF_UP));
            totalAmout = totalAmout.add(group.getMinPrice().multiply(new BigDecimal(preOrder.getNum()[i])).setScale(2,BigDecimal.ROUND_HALF_UP));
            origintotalAmout = origintotalAmout.add(group.getMaxPrice().multiply(new BigDecimal(preOrder.getNum()[i])).setScale(2,BigDecimal.ROUND_HALF_UP));
            totalNum+=preOrder.getNum()[i];
        }
        //2、冻结库存
        for(int i=0;i<preOrder.getSkuId().length;i++){
            Long skuId = preOrder.getSkuId()[i];
            Integer num = preOrder.getNum()[i].intValue();
            DistributedLock lock = distributedLockUtil.getDistributedLock(skuId.toString());
            try {
                if (lock.acquire()) {
                    //获取锁成功业务代码
                    SmallSku spu = (SmallSku) smallSkuService.getById(skuId);
//                    SmallSpu spu = (SmallSpu) smallSpuService.getById(skuId);
                    spu.setFreezeStock(spu.getFreezeStock()!=null?spu.getFreezeStock()+num:num);
                    boolean result = smallSkuService.updateById(spu);
                    if(!result){
                        log.error("兑换时,更新商品库存失败");
                        throw new ApiException("更新商品库存失败");
                    }
                } else { // 获取锁失败
                    //获取锁失败业务代码
                    throw new ApiException("系统繁忙,请稍后再试");
                }
            } finally {
                if (lock != null) {
                    lock.release();
                }
            }
        }
        //生成订单
        SmallOrder order = new SmallOrder();
        order.setChannel("小程序商品购买");
        order.setActualPrice(totalAmout);
        order.setSkuTotalPrice(totalAmout);
        order.setSkuOriginalTotalPrice(origintotalAmout);
//        order.setAddress(address.getAddress());
//        order.setCounty(address.getCounty());
//        order.setCity(address.getCity());
//        order.setPhone(address.getPhone());
//        order.setProvince(address.getProvince());
//        order.setConsignee(address.getName());
        order.setUserId(user.getId().longValue());
        order.setOrderStatus(0);
        order.setCreateTime(new Date());
        order.setMemo(preOrder.getMemo());
        order.setShopId(preOrder.getShopId());
        order.setOrderType("0");
        order.setOrderNo(SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort()%31L));
        order.setOrderStatus(0);//
        order.setPayStatus(0);
        order.setPayChannel("微信支付");
        order.setRefundStatus(0);
        order.setFreightPrice(new BigDecimal(0));
        order.setCouponPrice(new BigDecimal(0));
        order.setShipStatus(0);
        smallOrderMapper.insert(order);

        String productInfo = "";
        for(int i=0;i<preOrder.getSkuId().length;i++){
//            SmallSpu spu = (SmallSpu) smallSpuService.getById(preOrder.getSkuId()[i]);
            SmallSku sku = (SmallSku) smallSkuService.getById(preOrder.getSkuId()[i]);
            SmallGroupShop group = (SmallGroupShop) smallGroupShopService.getById(preOrder.getGroupId()[i]);
            SmallSpu spus = (SmallSpu) smallSpuService.getById(sku.getSpuId());
            SmallOrderDetail detail = new SmallOrderDetail();
            detail.setCreateTime(new Date());
            detail.setNum(preOrder.getNum()[i].intValue());
            detail.setOrderNo(order.getOrderNo());
            detail.setOrderId(order.getId());
            detail.setGroupId(preOrder.getGroupId()[i]);
            detail.setSkuId(preOrder.getSkuId()[i]);
            detail.setSkuTitle(sku.getTitle());
            detail.setSpuImg(sku.getImg()!=null?sku.getImg(): spus.getImg());
            detail.setPrice(group.getMinPrice());
            productInfo+=sku.getTitle()+"x"+detail.getNum()+";";
//            detail
            smallOrderDetailService.save(detail);
        }
        //清空对应的购物车
        for(int i=0;i<preOrder.getSkuId().length;i++){
            smallCartService.remove(new QueryWrapper<SmallCart>()
                    .eq("user_id",user.getId())
                    .eq("group_id",preOrder.getGroupId()[i])
                    .eq("sku_id",preOrder.getNum()[i])
                    .eq("shop_id",preOrder.getShopId()));

        }
        Shop shop = (Shop) shopService.getById(preOrder.getShopId());

        //生成发送通知消息任务，设置需要发送的消息
        //SmallPlaceOrderNotifyService smallPlaceOrderNotifyService = new SmallPlaceOrderNotifyService();
        //smallPlaceOrderNotifyService.setNotifyInof(order,user,shop,productInfo);
        //异步执行发送任务
       // ThreadPoodExecuteService.getTaskExecutor().execute(smallPlaceOrderNotifyService);

        return R.ok().put("orderNo",order.getOrderNo());
    }


    public boolean changeOrderStatus(String orderNo, Integer nowOrderStatus,Integer nowPayStatus, SmallOrder orderDO) throws ApiException {
        try {
            // 防止传入值为空,导致其余订单被改变
            if(orderNo == null || orderDO == null){
                throw new ApiException("修改订单状态:订单编号"+orderNo+",查询不到订单");
            }
            if (lockComponent.tryLock(ORDER_STATUS_LOCK + orderNo, 30)) {
                if (smallOrderMapper.update(orderDO,
                        new UpdateWrapper<SmallOrder>()
                                .eq("order_no", orderNo)
                                .eq("order_status", nowOrderStatus)
                                .eq("pay_status", nowPayStatus) ) > 0) {
                    return true;
                }
                throw new ApiException("修改订单状态失败");
            } else {
                throw new ApiException("获取订单锁失败");
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[订单状态扭转] 异常", e);
            throw new ApiException("[订单状态扭转] 异常");
        } finally {
            lockComponent.release(ORDER_STATUS_LOCK + orderNo);
        }
    }

    /**
     * 订单日报、订单月报
     * @param pageNo
     * @param pageSize
     * @param query
     * @return
     */
    public PageUtils findByPageReport(int pageNo, int pageSize, Query query) {
        PageHelper.startPage(pageNo, pageSize);
        List<OrderReportVo> list = smallOrderMapper.queryReportList(query);
        PageInfo<OrderReportVo> pageInfo = new PageInfo<OrderReportVo>(list);
        PageUtils<OrderReportVo> page = new PageUtils<OrderReportVo>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    /**
     * 订单日报、订单月报
     * @param pageNo
     * @param pageSize
     * @param query
     * @return
     */
    public PageUtils findByPageMonthReport(int pageNo, int pageSize, Query query) {
        PageHelper.startPage(pageNo, pageSize);
        List<OrderReportVo> list = smallOrderMapper.queryReportMonthList(query);
        PageInfo<OrderReportVo> pageInfo = new PageInfo<OrderReportVo>(list);
        PageUtils<OrderReportVo> page = new PageUtils<OrderReportVo>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

}

