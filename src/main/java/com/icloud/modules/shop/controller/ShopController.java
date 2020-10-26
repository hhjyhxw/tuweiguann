package com.icloud.modules.shop.controller;

import java.math.BigDecimal;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.DataFilter;
import com.icloud.annotation.SysLog;
import com.icloud.basecommon.model.Query;
import com.icloud.common.Constant;
import com.icloud.common.SnowflakeUtils;
import com.icloud.common.util.StringUtil;
import com.icloud.config.DeptUtils;
import com.icloud.config.ServerConfig;
import com.icloud.config.ShopFilterUtils;
import com.icloud.exceptions.BaseException;
import com.icloud.modules.bsactivity.entity.BsactivityAd;
import com.icloud.modules.bsactivity.service.BsactivityAdService;
import com.icloud.modules.shop.entity.ShopBank;
import com.icloud.modules.shop.entity.ShopMan;
import com.icloud.modules.shop.service.ShopBankService;
import com.icloud.modules.shop.service.ShopManService;
import com.icloud.modules.small.entity.*;
import com.icloud.modules.small.service.*;
import com.icloud.modules.small.vo.ShopTreeVo;
import com.icloud.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;


/**
 * 店铺 
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 * 菜单主连接： modules/shop/shop.html
 */
@RestController
@RequestMapping("shop/shop")
public class ShopController extends AbstractController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private SmallWasteRecordService smallWasteRecordService;
    @Autowired
    private ShopFilterUtils shopFilterUtils;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private ShopBankService shopBankService;
    @Autowired
    private ShopManService shopManService;
    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCouponService smallCouponService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shop:list")
    public List<Shop> list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<Shop> list =shopService.queryShopTree(query);
        return list;
    }


    /**
     * 角色数据权限列表
     */
    @RequestMapping("/queryList")
    @RequiresPermissions("shop:shop:list")
    public R queryList(@RequestParam Map<String, Object> params){
//        if(getUserId() == Constant.SUPER_ADMIN) {
//            params.put(Constant.SQL_FILTER, shopFilterUtils.getSQLFilterForshopsell());
//        }
        List<Shop> list = shopService.queryList(params);
        List<ShopTreeVo> shopTreeVolist = new ArrayList<ShopTreeVo>();

        list.forEach(p->{
            ShopTreeVo shopvo = new ShopTreeVo();
            shopvo.setId(p.getId());
            shopvo.setName(p.getShopName());
            shopvo.setParentId(p.getParentId());
            shopvo.setParentName((p.getShopName()));
            shopTreeVolist.add(shopvo);
        });
        return R.ok().put("list", shopTreeVolist);
    }
    /**
     * 查询处需要提现的店铺名称 和店铺余额列表，方便店铺管理员提交提现
     * 只获取当前登陆用户所在
     */
    @RequestMapping("/withdrawlist")
    @RequiresPermissions("shop:shop:withdrawlist")
    public R withdrawlist(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        query.put("id",getUser().getShopId());
        PageUtils page = shopService.findByPage(query.getPageNum(),query.getPageSize(), query);
        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/withdrawinfo/{id}")
    @RequiresPermissions("shop:shop:withdrawinfo")
    public R withdrawinfo(@PathVariable("id") Long id){
        Shop shop = (Shop)shopService.getById(id);
        return R.ok().put("shop", shop);
    }
    /**
     * 查询处需要提现的店铺名称 和店铺余额列表，方便店铺管理员提交提现
     */
    @SysLog("提交体现申请")
    @RequestMapping("/withdraw")
    @RequiresPermissions("shop:shop:withdraw")
    public R withdraw(@RequestBody SmallWasteRecord smallWasteRecord){
        Shop shop = (Shop)shopService.getById(smallWasteRecord.getShopId());
        smallWasteRecord.setCreateBy(getUser().getUsername());
        smallWasteRecord.setCreateTime(new Date());
        ValidatorUtils.validateEntity(smallWasteRecord);
        //判断是否存在提现申请未处理
        List<SmallWasteRecord> list = smallWasteRecordService.list(new QueryWrapper<SmallWasteRecord>()
                .eq("shop_id",smallWasteRecord.getShopId())//店铺id
                .eq("waste_flag","2")//提现类型
                .eq("approve_flag","0"));//未审核
        if(list!=null && list.size()>0){
            throw new BaseException("您有提现记录正在审核中，不能再次提交");
        }
        if(smallWasteRecord.getAmount().compareTo(new BigDecimal(0))<=0){
            throw new BaseException("提现金额不能小于0");
        }
        if(shop.getBalance()==null){
            throw new BaseException("账户余额为空，不能提现");
        }
        if(shop.getBalance().compareTo(smallWasteRecord.getAmount())<0){
            throw new BaseException("提现金额不能大于店铺余额，不能提现");
        }
        smallWasteRecord.setOrderNo("T" + SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort() % 31L));
//        smallWasteRecordService.createWaste(smallWasteRecord);
        smallWasteRecordService.save(smallWasteRecord);
        return R.ok();
    }


    /**
     * 店铺树
     */
    @RequestMapping("/select")
    public R select(@RequestParam Map<String, Object> params){
        List<Shop> list = null;
            list = shopService.list(new QueryWrapper<Shop>());//当前登陆用户的
        List<ShopTreeVo> shopTreeVolist = new ArrayList<ShopTreeVo>();
        if(list!=null && list.size()>0){
            ShopTreeVo shopvo = null;
            for (Shop shop:list){
                shopvo = new ShopTreeVo();
                shopvo.setId(shop.getId());
                shopvo.setName(shop.getShopName());
                shopvo.setParentId(shop.getParentId());
                if(shop.getParentId()!=null){
                    Object parent = shopService.getById(shop.getId());
                    if(parent!=null){
                        shopvo.setParentName((((Shop)parent).getShopName()));
                    }
                }
                shopTreeVolist.add(shopvo);
            }
        }
        if(getUserId() == Constant.SUPER_ADMIN) {
            ShopTreeVo root = new ShopTreeVo();
            root.setOpen(true);
            root.setId(0L);
            root.setName("一级店铺");
            root.setParentId(-1L);
            shopTreeVolist.add(root);
        }
        return R.ok().put("list", shopTreeVolist);
    }

    /**
     * 用户关联的店铺
     */
    @RequestMapping("/selfshoplist")
    public R selfshoplist(@RequestParam Map<String, Object> params){
        List<Shop> list =  shopService.list(new QueryWrapper<Shop>().eq("id",getUser().getShopId()));//当前登陆用户的
        return R.ok().put("list", list);
    }

    /**
     * 列表
     * 拥有
     */
    @RequestMapping("/selectlist")
    public R selectlist(@RequestParam Map<String, Object> params){
        List<Shop> list = null;
//        if(Constant.SUPER_ADMIN==getUserId()){//超级管理员选择的
            list = shopService.list(new QueryWrapper<Shop>());//当前登陆用户的
//        }else{
//            list = shopService.list(new QueryWrapper<Shop>().in("id", shopFilterUtils.getShopIdAndSubList()));//当前登陆用户的
//        }
        return R.ok().put("list", list);
    }

//    /**
//     * 选择所属店铺
//     */
//    @RequestMapping("/select")
//    @RequiresPermissions("shop:shop:update")
//    public R select(){
//        List<Shop> retailList = shopService.list();
//        List<ShopTreeVo> list =  new ArrayList<ShopTreeVo>();
//        ShopTreeVo vo = null;
//        if(list!=null){
//            for (Shop shop : retailList) {
//                vo =  new ShopTreeVo();
//                vo.setId(shop.getId());
//                vo.setName(shop.getShopName());
//                vo.setParentId(null);
//                vo.setParentName(null);
//                list.add(vo);
//            }
//        }
//        return R.ok().put("retailList", list);
//    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("shop:shop:info")
    public R info(@PathVariable("id") Long id){
        Shop shop = (Shop)shopService.getById(id);

        return R.ok().put("shop", shop);
    }

    /**
     * 保存
     */
    @SysLog("添加店铺")
    @RequestMapping("/save")
    @RequiresPermissions("shop:shop:save")
    public R save(@RequestBody Shop shop){
        ValidatorUtils.validateEntity(shop);
        shop.setCreatedTime(new Date());
        shop.setCreatedBy(getUser().getUsername());

        //设置级别
        if(shop.getParentId().longValue()==0){
            shop.setShopLevel(1);
        }else{
            Shop parentshop = (Shop) shopService.getById(shop.getParentId());
            shop.setShopLevel(parentshop.getShopLevel()+1);
        }
        if(shop.getCommissionRate()==null){
            shop.setCommissionRate(new BigDecimal(0));
        }
        shopService.save(shop);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改店铺")
    @RequestMapping("/update")
    @RequiresPermissions("shop:shop:update")
    public R update(@RequestBody Shop shop){
        ValidatorUtils.validateEntity(shop);
        shop.setUpdatedTime(new Date());
        shop.setUpdatedBy(getUser().getUsername());
        shopService.updateById(shop);
        
        return R.ok();
    }

    /**
     * 关闭或者开启店铺
     */
    @SysLog("关闭或者开启店铺")
    @RequestMapping("/updateStatus")
    @RequiresPermissions("shop:shop:update")
    public R updateStatus(@RequestBody Shop shop){
        shop.setUpdatedTime(new Date());
        shop.setUpdatedBy(getUser().getUsername());
        shopService.updateById(shop);
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除店铺")
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shop:delete")
    public R delete(@RequestBody Long[] ids){
        if(ids==null || ids.length==0){
            return R.ok();
        }
        for (int i=0;i<ids.length;i++){
            //关联店铺
            List<Shop> list = shopService.list(new QueryWrapper<Shop>().eq("parent_id",ids[i]));
            if(list!=null && list.size()>0){
                return R.error("有子店铺不能删除！");
            }
            //关联商品库
            List<SmallOrder> smallOrderlist =  smallOrderService.list(new QueryWrapper<SmallOrder>().eq("shop_id",ids[i]));
            if(smallOrderlist!=null && smallOrderlist.size()>0){
                return R.error("有关联订单不能删除！");
            }
            //关联订单
            List<SmallSpu> smallSpulist =  smallSpuService.list(new QueryWrapper<SmallSpu>().eq("shop_id",ids[i]));
            if(smallSpulist!=null && smallSpulist.size()>0){
                return R.error("有关联商品不能删除！");
            }
            //关联银行卡
            List<ShopBank> shopBanklist =  shopBankService.list(new QueryWrapper<ShopBank>().eq("shop_id",ids[i]));
            if(shopBanklist!=null && shopBanklist.size()>0){
                return R.error("有关联银行卡不能删除！");
            }
            //关联店员
            List<ShopMan> shopManlist =  shopManService.list(new QueryWrapper<ShopMan>().eq("shop_id",ids[i]));
            if(shopManlist!=null && shopManlist.size()>0){
                return R.error("有关联店员不能删除！");
            }
            //关联广告
            List<BsactivityAd> bsactivityAdlist =  bsactivityAdService.list(new QueryWrapper<BsactivityAd>().eq("shop_id",ids[i]));
            if(bsactivityAdlist!=null && bsactivityAdlist.size()>0){
                return R.error("有关联广告不能删除！");
            }
            //关联优惠券
            List<SmallCoupon> smallCouponlist =  smallCouponService.list(new QueryWrapper<SmallCoupon>().eq("shop_id",ids[i]));
            if(smallCouponlist!=null && smallCouponlist.size()>0){
                return R.error("有关联优惠券不能删除！");
            }
        }
        shopService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
