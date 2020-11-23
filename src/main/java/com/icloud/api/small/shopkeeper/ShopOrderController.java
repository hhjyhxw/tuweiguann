package com.icloud.api.small.shopkeeper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.basecommon.model.Query;
import com.icloud.common.DateUtil;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.beanutils.ColaBeanUtils;
import com.icloud.modules.small.entity.SmallOrder;
import com.icloud.modules.small.entity.SmallOrderDetail;
import com.icloud.modules.small.service.SmallOrderDetailService;
import com.icloud.modules.small.service.SmallOrderService;
import com.icloud.modules.small.util.CartOrderUtil;
import com.icloud.modules.small.vo.OrderDetailVo;
import com.icloud.modules.small.vo.OrderVo;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("店铺订单")
@RestController
@RequestMapping("/api/shopOrder")
public class ShopOrderController {

    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private SmallOrderDetailService smallOrderDetailService;

    /**
     *店铺今日订单
     * @return
     */
    @CrossOrigin
    @ApiOperation(value="店铺今日订单", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页多少记录", required = false, paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/todayOrderlist",method = {RequestMethod.GET})
    public R todayOrderlist(@LoginUser WxUser user,String pageNum,String pageSize) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        Map parma = new HashMap();
        parma.put("page",pageNum);
        parma.put("limit",pageSize);
        parma.put("shopId",user.getShopMan().getShopId());
        Date date = DateUtil.getDateWithoutTime(DateUtil.formatTimestamp(new Date()));
        parma.put("startTime",date+" "+"00:00:00");
        parma.put("endTime",date+" "+"23:59:59");
        Query query = new Query(parma);
        List<SmallOrder> orderlistSum = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("shop_id",user.getShopMan().getShopId()));
        BigDecimal totalAmount = CartOrderUtil.getOrderTotalAmount(orderlistSum);
//        List<SmallOrder> orderlist = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("user_id",user.getId()));
        PageUtils<SmallOrder> page = smallOrderService.findByPage(query.getPageNum(),query.getPageSize(), query);
        List<SmallOrder> orderlist = (List<SmallOrder>) page.getList();
        if(orderlist!=null && orderlist.size()>0){
            orderlist.forEach(p->{
                List<SmallOrderDetail> detaillist =  smallOrderDetailService.list(new QueryWrapper<SmallOrderDetail>().eq("order_id",p.getId()));
                List<OrderDetailVo> detaillistvo = ColaBeanUtils.copyListProperties(detaillist , OrderDetailVo::new, (articleEntity, articleVo) -> {
                    // 回调处理
                });
                p.setDetaillist(detaillistvo);
                p.setShow(false);
            });
        }
        page.setList(orderlist);
        return R.ok().put("page", page).put("totalAmount",totalAmount);
    }

    /**
     * 店铺历史订单
     * @return
     */
    @CrossOrigin
    @ApiOperation(value="店铺历史订单", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页多少记录", required = false, paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/hisOrderlist",method = {RequestMethod.GET})
    public R hisOrderlist(@LoginUser WxUser user,String pageNum,String pageSize) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        Map parma = new HashMap();
        parma.put("page",pageNum);
        parma.put("limit",pageSize);
        parma.put("userId",user.getId());
        parma.put("shopId",user.getShopMan().getShopId());
        Query query = new Query(parma);
        List<SmallOrder> orderlistSum = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("shop_id",user.getShopMan().getShopId()));
        BigDecimal totalAmount = CartOrderUtil.getOrderTotalAmount(orderlistSum);

//        List<SmallOrder> orderlist = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("user_id",user.getId()));
        PageUtils<SmallOrder> page = smallOrderService.findByPage(query.getPageNum(),query.getPageSize(), query);
        List<SmallOrder> orderlist = (List<SmallOrder>) page.getList();
        if(orderlist!=null && orderlist.size()>0){
            orderlist.forEach(p->{
                List<SmallOrderDetail> detaillist =  smallOrderDetailService.list(new QueryWrapper<SmallOrderDetail>().eq("order_id",p.getId()));
                List<OrderDetailVo> detaillistvo = ColaBeanUtils.copyListProperties(detaillist , OrderDetailVo::new, (articleEntity, articleVo) -> {
                    // 回调处理
                });
                p.setDetaillist(detaillistvo);
                p.setShow(false);
            });
        }
        page.setList(orderlist);
        return R.ok().put("page", page).put("totalAmount",totalAmount);
    }

    /**
     * 订单详情
     * @return
     */
    @CrossOrigin
    @ApiOperation(value="订单详情", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/orderdetail",method = {RequestMethod.GET})
    public R orderdetail(@RequestParam Long id,@LoginUser WxUser user) throws Exception {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        List<SmallOrder> orderlist = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("id",id));
        if(orderlist==null || orderlist.size()<=0){
            return R.error();
        }
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(orderVo,orderlist.get(0));
        List<SmallOrderDetail> detaillist =  smallOrderDetailService.list(new QueryWrapper<SmallOrderDetail>().eq("order_id",orderVo.getId()));
        List<OrderDetailVo> detaillistvo = ColaBeanUtils.copyListProperties(detaillist , OrderDetailVo::new, (articleEntity, articleVo) -> {
            // 回调处理
        });
        orderVo.setDetaillist(detaillistvo);
        return R.ok().put("order",orderVo);
    }


}
