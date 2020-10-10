//package com.icloud.api.small;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.icloud.annotation.AuthIgnore;
//import com.icloud.basecommon.model.Query;
//import com.icloud.basecommon.service.redis.RedisService;
//import com.icloud.common.PageUtils;
//import com.icloud.common.R;
//import com.icloud.common.util.StringUtil;
//import com.icloud.modules.bsactivity.entity.BsactivityAd;
//import com.icloud.modules.bsactivity.service.BsactivityAdService;
//import com.icloud.modules.shop.entity.Shop;
//import com.icloud.modules.shop.service.ShopService;
//import com.icloud.modules.small.service.SmallGroupShopService;
//import com.icloud.modules.small.service.SmallSpuService;
//import com.icloud.modules.small.vo.GroupSkuVo;
//import com.icloud.modules.wx.entity.WxUser;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//
//@Api("店铺与商品相关接口")
//@RestController
//@RequestMapping("/api/shop")
//public class ShopApiController_bak {
//
//    @Autowired
//    private BsactivityAdService bsactivityAdService;
//    @Autowired
//    private SmallGroupShopService smallGroupShopService;
//    @Autowired
//    private SmallSpuService smallSpuService;
//    @Autowired
//    private ShopService shopService;
//    @Autowired
//    private RedisService redisService;
//    @Autowired
//    private HttpServletRequest httpServletRequest;
//    /**
//     * 平台、店铺
//     * @return
//     */
////    @ApiOperation(value="平台、店铺广告", notes="")
////    @ApiImplicitParams({
////            @ApiImplicitParam(name = "supplierId", value = "商家id", required = false, paramType = "query", dataType = "Long"),
////    })
////    @RequestMapping(value = "/adlist",method = {RequestMethod.GET})
////    @ResponseBody
////    @AuthIgnore
////    public R shopInfo(@RequestParam Long supplierId)  {
////        if(supplierId==null){
////              List<Shop> shoplist = shopService.list(new QueryWrapper<Shop>().eq("sys_flag","1"));
////              if(shoplist!=null && shoplist.size()>0){
////                  supplierId = shoplist.get(0).getId();
////              }
////        }
////        if(supplierId==null){
////            return R.error("暂时没有广告");
////        }
////        List<BsactivityAd> list  = bsactivityAdService.list(new QueryWrapper<BsactivityAd>()
////                .eq("status",1)
////                .eq("supplier_id",supplierId));
////        return R.ok().put("list",list);
////    }
//
//
//    /**
//     * 1、平台店铺一定读取
//     * 获取平台 和当前店铺和分店列表
//     * @return
//     */
//    @ApiOperation(value="取平台 和当前店铺和分店列表", notes="")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "supplierId", value = "商家id", required = false, paramType = "query", dataType = "String"),
//    })
//    @RequestMapping(value = "/shoplist",method = {RequestMethod.GET})
//    @ResponseBody
//    @AuthIgnore
//    public R shoplist(String supplierId) {
//        Long shopId = null;
//        if(StringUtil.checkStr(supplierId) && !"null".equals(supplierId)){
//            shopId = Long.valueOf(supplierId);
//        }
//        //1、默认读取平台店铺
//        List<Shop> shoplist = new ArrayList<Shop>();
//        //系统店铺及系统店铺分店一定读取
//        List<Shop> sysshoplist = shopService.list(new QueryWrapper<Shop>().eq("sys_flag","1"));
//        if(sysshoplist!=null && sysshoplist.size()>0){
//            shoplist = sysshoplist;
//            List<Shop> sonlist = shopService.list(new QueryWrapper<Shop>().eq("parent_id",sysshoplist.get(0).getId()));
//            if(sonlist!=null && sonlist.size()>0){
//                shoplist.addAll(sonlist);
//            }
//        }
//        Long shopMainId = null;//用户自身店铺 或者分享店铺主id
//        String shopMainName = null;//用户自身店铺 或者分享店铺名称
//        String shopImg = null;//
//        //判断是否登录，登录后判断是否是店主
//        if(httpServletRequest.getHeader("accessToken")!=null){
//            Object sessuser = redisService.get(httpServletRequest.getHeader("accessToken"));
//            if(sessuser!=null){
//                WxUser user = (WxUser)sessuser;
//                List<Shop> mylist = shopService.list(new QueryWrapper<Shop>().eq("user_id",user.getId()));
//                if(mylist!=null && mylist.size()>0){
////                   if(mylist.get(0).getId().longValue()!=sysshoplist.get(0).getId().longValue()){
//                       shopId = mylist.get(0).getId();
////                       shopMainId = shopId;
////                       shopMainName = mylist.get(0).getShopName();
////                       shopImg = mylist.get(0).getShopImg();
////                   }
//                }
//            }
//        }
//
//        //查询店主店铺与分店
//       if(shopId!=null && shopId.longValue()!=sysshoplist.get(0).getId().longValue()){
//            //查询分享店铺和分店
//            Object shopobj = shopService.getById(shopId);
//            Shop shop = null;
//            if(shopobj!=null){
//                shop = (Shop)shopobj;
//                shopMainId = shopId;
//                shopMainName = shop.getShopName();
//                shopImg = shop.getShopImg();
//                shoplist.add(shop);
//                List<Shop> sonlist = shopService.list(new QueryWrapper<Shop>().eq("parent_id",shopId));
//                if(sonlist!=null && sonlist.size()>0){
//                    shoplist.addAll(sonlist);
//                }
//            }
//        }
//        List<BsactivityAd> adlist = null;
//        if(shopId!=null){
//            //查询店铺广告
//            adlist  = bsactivityAdService.list(new QueryWrapper<BsactivityAd>().eq("status",1).eq("supplier_id",shopId));
//        }
//        if(adlist==null || adlist.size()==0){
//            //店铺广告为空查询平台广告
//            adlist  = bsactivityAdService.list(new QueryWrapper<BsactivityAd>().eq("status",1).eq("supplier_id",sysshoplist.get(0).getId()));
//        }
//        //用于分享
//        if(shopMainId==null){
//            shopId = shoplist.get(0).getId();
//            shopMainId = shopId;
//            shopMainName = shoplist.get(0).getShopName();
//            shopImg = shoplist.get(0).getShopImg();
//
//        }
//        final long activeShop = shopMainId;
//        shoplist.forEach(p->{
//            if(p.getId().longValue()==activeShop){
//                p.setActive(true);
//            }else{
//                p.setActive(false);
//            }
//        });
//       return R.ok()
//               .put("shoplist",shoplist)
//               .put("adlist",adlist)
//               .put("supplierId",shopMainId)
//               .put("shopMainId",shopMainId)
//               .put("shopMainName",shopMainName)
//               .put("shopImg",shopImg);
//    }
//
//
//
//    /**
//     * 获取店铺商品列表（）
//     * 目前需要参数：
//     *   pageNum //第几页
//     *   pageSize  //每页条数
//     *   categoryId //分类id
//     *   supplierId //商户id (必填)
//     * @return
//     */
//    @ApiOperation(value="获取商品列表", notes="")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNum", value = "页码", required = false, paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name = "pageSize", value = "每页多少记录", required = false, paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name = "supplierId", value = "商户id", required = false, paramType = "query", dataType = "Long"),
//            @ApiImplicitParam(name = "keyword", value = "关键字", required = false, paramType = "query", dataType = "String"),
//
//    })
//    @RequestMapping(value = "/goodsList",method = {RequestMethod.GET})
//    @ResponseBody
//    @AuthIgnore
//    public R goodsList(String pageNum,String pageSize,Long supplierId,String keyword) {
//        //传入id为空则读取平台商品
//        if(supplierId==null){
//            List<Shop> shoplist = shopService.list(new QueryWrapper<Shop>().eq("sys_flag","1"));
//            if(shoplist!=null && shoplist.size()>0){
//                supplierId = shoplist.get(0).getId();
//            }
//        }
//        Query query = new Query(new HashMap<>());
//        query.put("status",1);
//        query.put("page",pageNum);
//        query.put("limit",pageSize);
//        query.put("supplierId",supplierId);
//        query.put("title",keyword);
//        query.put("",supplierId);
//        PageUtils<GroupSkuVo> page = smallGroupShopService.findByFrontPage(query.getPageNum(),query.getPageSize(), query);
//        return R.ok().put("page", page);
//    }
//
//}
