package com.icloud.modules.shop.controller;

import java.math.BigDecimal;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import com.icloud.common.Constant;
import com.icloud.common.SnowflakeUtils;
import com.icloud.common.util.StringUtil;
import com.icloud.config.DeptUtils;
import com.icloud.config.ServerConfig;
import com.icloud.exceptions.BaseException;
import com.icloud.modules.small.entity.SmallCategory;
import com.icloud.modules.small.entity.SmallWasteRecord;
import com.icloud.modules.small.service.SmallWasteRecordService;
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
    private DeptUtils deptUtils;
    @Autowired
    private ServerConfig serverConfig;
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("shop:shop:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = shopService.findByPage(query.getPageNum(),query.getPageSize(), query);
        List<Shop> list = page.getList();
        if(list!=null && list.size()>0){
            list.forEach(p->{
                if(p.getParentId()!=null){
                    Object parent = shopService.getById(p.getId());
                    if(parent!=null){
                        p.setParentName((((Shop)parent).getShopName()));
                    }
                }
            });
        }
        page.setList(list);
        return R.ok().put("page", page);
    }


    /**
     * 查询处需要提现的店铺名称 和店铺余额列表，方便店铺管理员提交提现
     * 只获取当前登陆用户所在
     */
    @RequestMapping("/withdrawlist")
    @RequiresPermissions("shop:shop:withdrawlist")
    @DataFilter
    public R withdrawlist(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
//        if(getUserId()!= Constant.SUPER_ADMIN){
//            query.put("deptId",getDeptId());
//        }
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
    @RequestMapping("/withdraw")
    @RequiresPermissions("shop:shop:withdraw")
    public R withdraw(@RequestBody SmallWasteRecord smallWasteRecord){
        Shop shop = (Shop)shopService.getById(smallWasteRecord.getShopId());
        smallWasteRecord.setDeptId(shop.getDeptId());
        smallWasteRecord.setCreateBy(getUser().getUsername());
        smallWasteRecord.setCreateTime(new Date());
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
    @RequiresPermissions("small:smallcategory:update")
    @DataFilter//超级管理员需要在页面传入企业id,非超级管理员根据当前登陆用户所在企业过滤获取对应企业数据
    public R select(@RequestParam Map<String, Object> params){
        List<Shop> list = null;
        //超级管理员可以为其他企业添加分类
        if(Constant.SUPER_ADMIN==getUserId() && StringUtil.checkObj(params.get("deptId"))){//超级管理员选择的
            list = shopService.list(new QueryWrapper<Shop>().in("dept_id", deptUtils.getDeptIdLists(Long.valueOf(params.get("deptId").toString()))));
        }else{
            list = shopService.list(new QueryWrapper<Shop>().in("dept_id", deptUtils.getDeptIdList()));//当前登陆用户的
        }

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
        return R.ok().put("retailList", shopTreeVolist);
    }
    /**
     * 列表
     */
    @RequestMapping("/selectlist")
    public R attibutList(@RequestParam Map<String, Object> params){
        //超级管理员可以为其他企业添加分类
        List<Shop> list = null;
        if(Constant.SUPER_ADMIN==getUserId() && StringUtil.checkObj(params.get("deptId"))){//超级管理员选择的
            list = shopService.list(new QueryWrapper<Shop>().in("dept_id", deptUtils.getDeptIdLists(Long.valueOf(params.get("deptId").toString()))));
        }else{
            list = shopService.list(new QueryWrapper<Shop>().in("dept_id", deptUtils.getDeptIdList()));//当前登陆用户的
        }
//        List<Shop> list = shopService.list(new QueryWrapper<Shop>().in("dept_id", deptUtils.getDeptIdList()));
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
    @RequestMapping("/save")
    @RequiresPermissions("shop:shop:save")
    public R save(@RequestBody Shop shop){
        ValidatorUtils.validateEntity(shop);
        shop.setCreatedTime(new Date());
        shop.setCreatedBy(getUser().getUsername());
        /*设置部门*/
        if(shop.getDeptId()==null){
            shop.setDeptId(getDeptId());
        }
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
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("shop:shop:delete")
    public R delete(@RequestBody Long[] ids){
        shopService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
