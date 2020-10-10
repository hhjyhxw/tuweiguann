package com.icloud.api.small;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.api.vo.QuerySkuCategoryVo;
import com.icloud.api.vo.SkuSpuCategoryVo;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.beanutils.ColaBeanUtils;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.exceptions.ApiException;
import com.icloud.modules.small.entity.*;
import com.icloud.modules.small.service.*;
import com.icloud.modules.small.util.CartOrderUtil;
import com.icloud.modules.small.vo.*;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api("订单相关接口")
@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallGroupShopService smallGroupShopService;
    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private SmallOrderDetailService smallOrderDetailService;
    @Autowired
    private SmallAddressService smallAddressService;
    @Autowired
    private SmallCouponService smallCouponService;
    @Autowired
    private SmallUserCouponService smallUserCouponService;
    @Autowired
    private SmallSkuService smallSkuService;
    @Autowired
    private SmallCategoryService smallCategoryService;

    @Deprecated
    @ApiOperation(value="订单确认", notes="")
    @RequestMapping(value = "/preOrder",method = {RequestMethod.POST})
    @ResponseBody
    public R preOrder(@RequestBody PresOrder preOrder, @LoginUser WxUser user)  {
        try{
            ValidatorUtils.validateEntityForFront(preOrder);
            if(preOrder.getNum()==null || preOrder.getNum().length==0 || preOrder.getSkuId()==null
                    || preOrder.getNum().length!=preOrder.getSkuId().length){
                return R.error("参数不正确");
            }
            SmallAddress address = null;
            List<SmallAddress> addressList = smallAddressService.list(new QueryWrapper<SmallAddress>().eq("user_id",user.getId()));
            for (SmallAddress temp:addressList){
                if(temp.getDefaultAddress()!=null && temp.getDefaultAddress().intValue()==1){
                    address = temp;
                    break;
                }
            }
            if(address==null && addressList!=null && addressList.size()>0){
                address = addressList.get(0);
            }

            List<CartVo> list  = new ArrayList<CartVo>();
            for(int i=0;i<preOrder.getSkuId().length;i++){
                CartVo vo = new CartVo();
                SmallSpu spu = (SmallSpu) smallSpuService.getById(preOrder.getSkuId()[i]);
                BeanUtils.copyProperties(vo,spu);
                vo.setNum(preOrder.getNum()[i].intValue());
                vo.setUserId(user.getId().longValue());
                if(vo.getNum().intValue()<=0){
                    vo.setNum(1);
                }
                vo.setSkuId(vo.getId());
                vo.setId(null);
                list.add(vo);
            }
            CartTotalVo total = CartOrderUtil.getTotal(list);
            return R.ok().put("list",list).put("totalAmout",total.getTotalAmout()).put("totalNum",total.getTotalNum()).put("address",address);
        }catch (Exception e){
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    @CrossOrigin
    @ApiOperation(value="生成订单", notes="")
    @RequestMapping(value = "/createOrder",method = {RequestMethod.POST})
    @ResponseBody
    public R createOrder(@RequestBody CreateOrder preOrder, @LoginUser WxUser user) {
        try {
            SmallUserCoupon userCoupon = null;
            SmallCoupon smallCoupon = null;

            if(preOrder.getNum()==null || preOrder.getNum().length==0 || preOrder.getSkuId()==null || preOrder.getNum().length!=preOrder.getSkuId().length){
                return R.error("商品数量与商品id不匹配");
            }
            if(preOrder.getMycouponId()!=null){
                 userCoupon = (SmallUserCoupon) smallUserCouponService.getById(preOrder.getMycouponId());
                 smallCoupon = (SmallCoupon) smallCouponService.getById(userCoupon.getCouponId());
                //判断是否是新用户专用券
                if(smallCoupon.getSurplus().intValue()==1){
                    //判断是否已在该店铺消费过，消费过，则不再显示新用户专用券
                    List<SmallOrder> list = smallOrderService.list(new QueryWrapper<SmallOrder>()
                            .eq("supplier_id",preOrder.getSupplierId())
                            .eq("coupon_id",userCoupon.getId())
                            .eq("user_id",user.getId()));
                    if(list!=null && list.size()>0){
                        return R.error("已使用该类型优惠券，不能再使用");
                    }
                }else{
                    //校验是否所有商品都适合使用该优惠券; 优惠券分类id为空，适合所有店铺所有商品；不为空校验是否所有商品适合优惠券
                    if(smallCoupon.getCategoryId()!=null){
                        checkCateory(preOrder,smallCoupon.getCategoryId());
                    }
                }
            }
            //暂时不用收货地址
            SmallAddress address = null;
//            List<SmallAddress> addressList = smallAddressService.list(new QueryWrapper<SmallAddress>().eq("user_id",user.getId()).eq("id",preOrder.getAddressId()));
//            if(addressList==null || addressList.size()<=0){
//                return R.error("收货地址不能为空");
//            }
//            address = addressList.get(0);
                //2、生成订单、冻结库存
            return  smallOrderService.createOrder(preOrder,user,address,userCoupon,smallCoupon);
        }catch (Exception e){
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * //存在优惠券情况下，校验是否存在公共商品，有公共商品 不能使用优惠券
     * @param preOrder
     */
    private void checkCommonFlag(CreateOrder preOrder){
      List<SmallGroupShop> list = smallGroupShopService.list(new QueryWrapper<SmallGroupShop>().in("id",preOrder.getGroupId()));
       for (SmallGroupShop goods:list){
           if("1".equals(goods.getCommonFlag())){
               log.info("团购商品是公共商品:id="+ goods.getId()+",不能使用优惠券");
               throw new ApiException("商品不满足使用使用优惠券条件");
           }
       }
        //比较
    }

    //存在优惠券情况下，校验商品是否符合使用条件
    private void checkCateory(CreateOrder preOrder,Long categoryId){
        QuerySkuCategoryVo vo = new QuerySkuCategoryVo();
        vo.setSkuId(preOrder.getSkuId());
        vo.setSupplierId(preOrder.getSupplierId());
        List<SkuSpuCategoryVo>  list = smallSkuService.getSkuAndCategoryList(vo);
        List<SkuSpuCategoryVo>  exislist = new ArrayList<>();
        log.info("SkuSpuCategoryVoList==="+ JSON.toJSONString(list));
        Map<String, Object> map = new HashMap<>();
        map.put("id",categoryId);
        List<SmallCategory>  couponCategoryList =smallCategoryService.queryList(map);
        log.info("couponCategoryList==="+ JSON.toJSONString(couponCategoryList));
        int k = 0;
        for (int i = 0; i <list.size(); i++) {
            for (int j = 0; j <couponCategoryList.size() ; j++) {
                if(list.get(i).getCategoryId().longValue()==couponCategoryList.get(j).getId().longValue()){
                    k++;
                    exislist.add(list.get(i));
                    continue;
                }
            }
        }
        if(list.size()!=k){
            for (int i = 0; i <exislist.size() ; i++) {
                list.remove(exislist.get(i));
            }
            throw new ApiException(list.get(0).getTitle()+"不能使用优惠券");
        }
        //比较
    }

    /**
     * 最近订单
     * @return
     */
    @CrossOrigin
    @ApiOperation(value="最近订单", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "店铺id", required = false, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/latesOrder",method = {RequestMethod.GET})
    @ResponseBody
    public R latesOrder(@LoginUser WxUser user,Long supplierId) throws Exception {
        SmallOrder smallOrder = null;
        List<SmallOrder> list = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("supplier_id",supplierId).orderByDesc("create_time"));
        if(list!=null && list.size()>0){
            smallOrder = list.get(0);
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(orderVo,smallOrder);
            List<SmallOrderDetail> detaillist =  smallOrderDetailService.list(new QueryWrapper<SmallOrderDetail>().eq("order_id",orderVo.getId()));
            List<OrderDetailVo> detaillistvo = ColaBeanUtils.copyListProperties(detaillist , OrderDetailVo::new, (articleEntity, articleVo) -> {
                // 回调处理
            });
            orderVo.setDetaillist(detaillistvo);
            return R.ok().put("order",orderVo);
        }
        return R.ok().put("order",null);
    }

    /**
     * 用户历史订单订单列表
     * @return
     */
    @CrossOrigin
    @ApiOperation(value="用户订单列表", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页多少记录", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "supplierId", value = "店铺id", required = false, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/orderlist",method = {RequestMethod.GET})
    @ResponseBody
    public R orderlist(@LoginUser WxUser user,String pageNum,String pageSize,Long supplierId) {
        Map parma = new HashMap();
        parma.put("page",pageNum);
        parma.put("limit",pageSize);
        parma.put("userId",user.getId());
        parma.put("supplierId",supplierId);
        Query query = new Query(parma);
        List<SmallOrder> orderlistSum = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("user_id",user.getId())
                .eq("supplier_id",supplierId));
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
    @ResponseBody
    public R orderdetail(@RequestParam Long id,@LoginUser WxUser user) throws Exception {

        List<SmallOrder> orderlist = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("user_id",user.getId()).eq("id",id));
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

    /**
     * 总下单金额
     * @return
     */
    @CrossOrigin
    @ApiOperation(value="总下单金额", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplierId", value = "店铺id", required = false, paramType = "query", dataType = "long")
    })
    @RequestMapping(value = "/getTotalOrderAmount",method = {RequestMethod.GET})
    @ResponseBody
    public R getTotalOrderAmount(@LoginUser WxUser user,Long supplierId) {
        List<SmallOrder> orderlistSum = smallOrderService.list(new QueryWrapper<SmallOrder>().eq("user_id",user.getId())
                .eq("supplier_id",supplierId));
        BigDecimal totalAmount = CartOrderUtil.getOrderTotalAmount(orderlistSum);
        return R.ok().put("totalAmount",totalAmount);
    }


}
