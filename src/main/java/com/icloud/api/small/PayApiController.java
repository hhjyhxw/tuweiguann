package com.icloud.api.small;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.icloud.annotation.AuthIgnore;
import com.icloud.annotation.LoginUser;
import com.icloud.basecommon.model.Query;
import com.icloud.common.*;
import com.icloud.common.beanutils.ColaBeanUtils;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.config.global.mini.WxMaProperties;
import com.icloud.config.global.wx.WxMpProperties;
import com.icloud.exceptions.ApiException;
import com.icloud.modules.small.entity.SmallAddress;
import com.icloud.modules.small.entity.SmallOrder;
import com.icloud.modules.small.entity.SmallOrderDetail;
import com.icloud.modules.small.entity.SmallSpu;
import com.icloud.modules.small.service.*;
import com.icloud.modules.small.util.CartOrderUtil;
import com.icloud.modules.small.vo.*;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Api("支付相关")
@RestController
@RequestMapping("/api/pay")
public class PayApiController {

    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private WxMaProperties wxMaProperties;
    @Autowired
    private WxNotifyService wxNotifyService;

    /**
     * 小程序预支付
     * @param orderNo
     * @param user
     * @return
     * @throws ApiException
     */
    @ApiOperation(value="根据订单号获取与支付单", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/wxPrepay",method = {RequestMethod.POST})
    @ResponseBody
    public R wxPrepay(@RequestParam String orderNo,@LoginUser WxUser user) throws ApiException {
        Date now = new Date();
        List<SmallOrder> orderList = smallOrderService.list(new QueryWrapper<SmallOrder>()
                .eq("user_id",user.getId())
                .eq("order_no",orderNo));
        if(orderList==null || orderList.size()==0){
            return R.error("订单不存在");
        }
        SmallOrder order = orderList.get(0);
        // 检测订单状态
        if (0!=order.getPayStatus().intValue()) {
            log.info("订单状态 status=="+order.getPayStatus());
            throw new ApiException("订单状态不能支付");
        }

        Object result = null;
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            log.info("wxMaProperties.getConfigs().get(0).getAppId()==="+ wxMaProperties.getConfigs().get(0).getAppid());
            orderRequest.setAppid(wxMaProperties.getConfigs().get(0).getAppid());
            orderRequest.setOutTradeNo(orderNo);
            orderRequest.setOpenid(user.getXcxopenid());
            orderRequest.setBody("订单：" + orderNo);
            orderRequest.setTotalFee(PayUtil.getFinalmoneyInt(String.valueOf(order.getActualPrice())));
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));
            orderRequest.setTradeType(WxPayConstants.TradeType.JSAPI);//公众号支付
            result = wxPayService.createOrder(orderRequest);
            log.info("wxPrepay获取预支付单请求结果=="+ JSON.toJSONString(result));
        } catch (WxPayException e) {
            log.error("[微信支付] 异常1", e);
            throw new ApiException("预付款异常:"+e.getErrCodeDes());
        } catch (Exception e) {
            log.error("[预付款异常]2", e);
            throw new ApiException(e.getMessage());
        }
        return R.ok().put("payparam",result);
    }

    @ApiOperation(value="线下支付", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = "query", dataType = "String")
    })
    @RequestMapping("/offlinePrepay")
    @ResponseBody
    public R offlinePrepay(@RequestParam String orderNo,@LoginUser WxUser user) throws ApiException {
        List<SmallOrder> orderList = smallOrderService.list(new QueryWrapper<SmallOrder>()
                .eq("user_id",user.getId())
                .eq("order_no",orderNo));
        if(orderList==null || orderList.size()==0){
            return R.error("订单不存在");
        }
        SmallOrder order = orderList.get(0);
        // 检测订单状态
        if (0!=order.getPayStatus().intValue()) {
            log.info("订单状态 status=="+order.getPayStatus());
            throw new ApiException("订单状态不能支付");
        }
        SmallOrder updateOrderDO = new SmallOrder();
        updateOrderDO.setPayChannel("OFFLINE");
        updateOrderDO.setModifyTime(new Date());
        updateOrderDO.setPayStatus(2);//已支付
        updateOrderDO.setOrderStatus(1);//处理中，未配送

        boolean result = smallOrderService.changeOrderStatus(orderNo, 0,0, updateOrderDO);
//        List<SmallOrderDetail> orderSkuDOList = mallOrderDetailService.list(
//                new QueryWrapper<SmallOrderDetail>()
//                        .eq("order_no", orderNo));
//        orderSkuDOList.forEach(item -> {
//            //增加销量
//            smallSpuService.incSales(item.getSpuId(), item.getNum());
//        });
        if (result) {
            return R.ok("ok");
        }
        throw new ApiException("线下支付更新失败");
    }


    @AuthIgnore
    @RequestMapping("/notify")
    public Object wxpay(@RequestBody String body) throws Exception {
        WxPayOrderNotifyResult result = null;
        try {
            result = wxPayService.parseOrderNotifyResult(body);
        } catch (WxPayException e) {
            log.error("[微信解析回调请求] 异常", e);
            return WxPayNotifyResponse.fail(e.getMessage());
        }
        log.info("处理微信支付平台的订单支付");
        log.info(JSONObject.toJSONString(result));
        return wxNotifyService.wxpaynotify(result);
    }
}
