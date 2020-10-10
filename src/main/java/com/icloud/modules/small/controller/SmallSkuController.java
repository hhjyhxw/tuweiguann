package com.icloud.modules.small.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.basecommon.model.Query;
import com.icloud.common.util.StringUtil;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.small.entity.SmallSpu;
import com.icloud.modules.small.service.SmallSpuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.icloud.modules.small.entity.SmallSku;
import com.icloud.modules.small.service.SmallSkuService;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;


/**
 * 商品sku
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 * 菜单主连接： modules/small/smallsku.html
 */
@Slf4j
@RestController
@RequestMapping("small/smallsku")
public class SmallSkuController {
    @Autowired
    private SmallSkuService smallSkuService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private ShopService shopService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallspu:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallSkuService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }

    /**
     *
     * @param params
     * @return
     */
    @RequestMapping("/listForgroup")
    public R listForgroup(@RequestParam Map<String, Object> params){
        Long supplierId = Long.valueOf(params.get("supplierId").toString());//当前需要商家商品的店铺
        Shop shop = (Shop) shopService.getById(supplierId);
        //查询到复合需求的spuList集合
        List<SmallSpu> spuList = null;
        //非系统店铺 上架系统店铺商品
        if(StringUtil.checkObj(params.get("sysFlag")) && Boolean.parseBoolean(params.get("sysFlag").toString()) && !"1".equals(shop.getSysFlag())){
            List<Shop> shoplist = shopService.list(new QueryWrapper<Shop>().eq("sys_flag","1"));
            if(shoplist!=null && shoplist.size()>0){
                List<Long> shopIds = new ArrayList<>();
                shoplist.forEach(p->{
                    shopIds.add(p.getId());
                });
                spuList = smallSpuService.list(new QueryWrapper<SmallSpu>().in("supplier_id",shopIds));//所有系统店铺的spu
            }
        }else{
            spuList = smallSpuService.list(new QueryWrapper<SmallSpu>().eq("supplier_id",supplierId));//自营商品的
        }
        if(spuList==null || spuList.size()==0){
            return R.error();
        }
        //结果集
        List<Long> spuIds = new ArrayList<>();
        //遍历集合取值
        spuList .forEach(item->{
            spuIds.add(item.getId());
        });
        Object title = params.get("title");
        Query query = new Query(params);
        PageUtils page = smallSkuService.listForgroupPage(query.getPageNum(),query.getPageSize(), spuIds,StringUtil.checkObj(title)?title.toString():null);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/skulist")
    @RequiresPermissions("small:smallspu:list")
    public R skulist(@RequestParam Long spuId){
        List<SmallSku> list = smallSkuService.list(new QueryWrapper<SmallSku>().eq("spu_id",spuId));
        list.forEach(p->{
            Integer remainStock = (p.getStock()!=null?p.getStock().intValue():0) - (p.getFreezeStock()!=null?p.getFreezeStock().intValue():0);
            p.setRemainStock(remainStock>0?remainStock:0);
        });
        return R.ok().put("list", list);
    }

    /**
     * 添加团购商品使用列表
     */
    /**
     *
     * @param supplierId  上架团购商品的店铺Id
     * @param sysFlag
     * @return
     */
    @RequestMapping("/skulistForGroup")
    @RequiresPermissions("small:smallspu:list")
    public R skulistForGroup(@RequestParam Long supplierId,@RequestParam boolean sysFlag){
        Shop shop = (Shop) shopService.getById(supplierId);
        //查询到list集合
        List<SmallSpu> spuList = null;
        //非系统店铺 上架系统店铺商品
        if(sysFlag && !"1".equals(shop.getSysFlag())){
            List<Shop> shoplist = shopService.list(new QueryWrapper<Shop>().eq("sys_flag","1"));
            if(shoplist!=null && shoplist.size()>0){
                List<Long> shopIds = new ArrayList<>();
                shoplist.forEach(p->{
                    shopIds.add(p.getId());
                });
                spuList = smallSpuService.list(new QueryWrapper<SmallSpu>().in("supplier_id",shopIds));
            }
        }else{
            spuList = smallSpuService.list(new QueryWrapper<SmallSpu>().eq("supplier_id",supplierId));
        }
        if(spuList==null || spuList.size()==0){
            return R.error();
        }
        //结果集
        List<Long> spuIds = new ArrayList<>();
        //遍历集合取值
        spuList .forEach(item->{
            spuIds.add(item.getId());
        });
        List<SmallSku> list = smallSkuService.list(new QueryWrapper<SmallSku>().in("spu_id",spuIds));
        list.forEach(p->{
            Integer remainStock = (p.getStock()!=null?p.getStock().intValue():0) - (p.getFreezeStock()!=null?p.getFreezeStock().intValue():0);
            p.setRemainStock(remainStock>0?remainStock:0);
        });
        return R.ok().put("list", list);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallspu:info")
    public R info(@PathVariable("id") Long id){
        SmallSku smallSku = (SmallSku)smallSkuService.getById(id);
        Integer remainStock = (smallSku.getStock()!=null?smallSku.getStock().intValue():0) - (smallSku.getFreezeStock()!=null?smallSku.getFreezeStock().intValue():0);
        smallSku.setRemainStock(remainStock>0?remainStock:0);
        return R.ok().put("smallSku", smallSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("small:smallspu:save")
    public R save(@RequestBody SmallSku smallSku){
        ValidatorUtils.validateEntity(smallSku);

        log.info("smallSpu==="+ JSON.toJSONString(smallSku));
        if(smallSku.getAddStock()!=null && smallSku.getAddStock()>0){
            smallSku.setFreezeStock(0);
            smallSku.setStock(smallSku.getAddStock());
        }else{
            smallSku.setStock(0);
            smallSku.setFreezeStock(0);
        }
        smallSkuService.save(smallSku);
        smallSku.setRemainStock(smallSku.getStock()>0?smallSku.getStock():0);
        return R.ok().put("smallSku", smallSku);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("small:smallspu:update")
    public R update(@RequestBody SmallSku smallSku){
        ValidatorUtils.validateEntity(smallSku);
        log.info("smallSpu==="+ JSON.toJSONString(smallSku));

        //增加总库存
        if(smallSku.getAddStock()!=null && smallSku.getAddStock()>0){
            smallSku.setStock(smallSku.getStock()!=null?smallSku.getStock().intValue()+smallSku.getAddStock().intValue():smallSku.getAddStock());
            //减少总库存
        }else if(smallSku.getAddStock()!=null && smallSku.getAddStock()<=0){
            Integer stock = smallSku.getStock()!=null?smallSku.getStock().intValue()+smallSku.getAddStock().intValue():smallSku.getAddStock();
            //总库存 不能小于已 冻结库存
            if(stock<=0){
                smallSku.setStock(smallSku.getFreezeStock()!=null?smallSku.getFreezeStock():0);
            }else{
                smallSku.setStock(stock-(smallSku.getFreezeStock()!=null?smallSku.getFreezeStock():0)>0?stock:smallSku.getFreezeStock());
            }
        }
        smallSkuService.updateById(smallSku);
        Integer remainStock = (smallSku.getStock()!=null?smallSku.getStock().intValue():0) - (smallSku.getFreezeStock()!=null?smallSku.getFreezeStock().intValue():0);
        smallSku.setRemainStock(remainStock>0?remainStock:0);
        return R.ok().put("smallSku", smallSku);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallspu:delete")
    public R delete(@RequestBody Long[] ids){
        smallSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
