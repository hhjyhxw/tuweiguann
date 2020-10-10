package com.icloud.modules.small.controller;

import com.alibaba.fastjson.JSON;
import com.icloud.annotation.DataFilter;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.small.entity.SmallSku;
import com.icloud.modules.small.entity.SmallSpu;
import com.icloud.modules.small.service.SmallSkuService;
import com.icloud.modules.small.service.SmallSpuService;
import com.icloud.modules.sys.controller.AbstractController;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;


/**
 * 商品spu
 *
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-08-13 14:34:02
 * 菜单主连接： modules/small/smallspu.html
 */
@Slf4j
@RestController
@RequestMapping("small/smallspu")
public class SmallSpuController extends AbstractController {
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private ShopService shopService;
    @Autowired
    private SmallSkuService smallSkuService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("small:smallspu:list")
    @DataFilter
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        PageUtils page = smallSpuService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/getSessionId")
    public R getSessionId(){
        String sessionId = httpServletRequest.getSession().getId();

        return R.ok().put("sessionId", sessionId);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("small:smallspu:info")
    public R info(@PathVariable("id") Long id){
        SmallSpu smallSpu = (SmallSpu)smallSpuService.getById(id);
        Integer remainStock = (smallSpu.getStock()!=null?smallSpu.getStock().intValue():0) - (smallSpu.getFreezeStock()!=null?smallSpu.getFreezeStock().intValue():0);
        smallSpu.setRemainStock(remainStock>0?remainStock:0);
        return R.ok().put("smallSpu", smallSpu);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("small:smallspu:save")
    public R save(@RequestBody SmallSpu smallSpu){
        ValidatorUtils.validateEntity(smallSpu);
        log.info("smallSpu==="+ JSON.toJSONString(smallSpu));
//         smallSpu.setDeptId(getDeptId());
        if(smallSpu.getAddStock()!=null && smallSpu.getAddStock()>0){
            smallSpu.setFreezeStock(0);
            smallSpu.setStock(smallSpu.getAddStock());
        }else{
            smallSpu.setStock(0);
            smallSpu.setFreezeStock(0);
        }
        smallSpu.setSales(0);
        smallSpu.setCreateTime(new Date());
        smallSpuService.save(smallSpu);

        SmallSku sku = new SmallSku();
        BeanUtils.copyProperties(smallSpu,sku);
        sku.setId(null);
        sku.setSpuId(smallSpu.getId());
        sku.setCreateTime(new Date());
        smallSkuService.save(sku);
        return R.ok().put("smallSpu",smallSpu);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("small:smallspu:update")
    public R update(@RequestBody SmallSpu smallSpu){
        log.info("smallSpu==="+ JSON.toJSONString(smallSpu));
//        ValidatorUtils.validateEntity(smallSpu);
         //增加总库存
        if(smallSpu.getAddStock()!=null && smallSpu.getAddStock()>0){
            smallSpu.setStock(smallSpu.getStock()!=null?smallSpu.getStock().intValue()+smallSpu.getAddStock().intValue():smallSpu.getAddStock());
        //减少总库存
        }else if(smallSpu.getAddStock()!=null && smallSpu.getAddStock()<=0){
            Integer stock = smallSpu.getStock()!=null?smallSpu.getStock().intValue()+smallSpu.getAddStock().intValue():smallSpu.getAddStock();
            //总库存 不能小于已 冻结库存
            if(stock<=0){
                smallSpu.setStock(smallSpu.getFreezeStock()!=null?smallSpu.getFreezeStock():0);
            }else{
                smallSpu.setStock(stock-(smallSpu.getFreezeStock()!=null?smallSpu.getFreezeStock():0)>0?stock:smallSpu.getFreezeStock());
            }
        }
        smallSpu.setModifyTime(new Date());
        smallSpuService.updateById(smallSpu);
        return R.ok().put("smallSpu",smallSpu);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("small:smallspu:delete")
    public R delete(@RequestBody Long[] ids){
//        smallSpuService.removeByIds(Arrays.asList(ids));
        return  smallSpuService.removeBatchByIds(Arrays.asList(ids));
//        return R.ok();
    }

}
