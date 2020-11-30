package com.icloud.api.small.shopkeeper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.icloud.annotation.LoginUser;
import com.icloud.api.dto.shopkeeper.BatchStatusdto;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.modules.bsactivity.service.BsactivityAdService;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.small.entity.SmallGroupShop;
import com.icloud.modules.small.entity.SmallOrderDetail;
import com.icloud.modules.small.entity.SmallSku;
import com.icloud.modules.small.entity.SmallSpu;
import com.icloud.modules.small.service.*;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Api("店铺商品")
@RestController
@RequestMapping("/api/shopgoods")
public class ShopGoodsController {

    @Autowired
    private BsactivityAdService bsactivityAdService;
    @Autowired
    private SmallCartService smallCartService;
    @Autowired
    private SmallSpuService smallSpuService;
    @Autowired
    private SmallSkuService smallSkuService;
    @Autowired
    private SmallGroupShopService smallGroupShopService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private SmallOrderService smallOrderService;
    @Autowired
    private SmallOrderDetailService smallOrderDetailService;
    /**
     * 店主商品库商品列表
     * @return
     */
    @ApiOperation(value="店主商品库商品列表", notes="")
    @RequestMapping(value = "/goodsSpuList",method = {RequestMethod.GET})
    public R goodsSpuList(@LoginUser WxUser user,@RequestBody Map<String,Object> params) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        List<SmallSpu> list = smallSpuService.list(new QueryWrapper<SmallSpu>().eq("shop_id",user.getShopMan().getShopId()));
        return R.ok().put("list",list);
    }

    /**
     * 商品库商品保存
     * @return
     */
    @ApiOperation(value="商品库商品添加", notes="")
    @RequestMapping(value = "/saveSpu",method = {RequestMethod.GET})
    public R addSpu(@RequestParam Long shopId,@LoginUser WxUser user,@RequestBody SmallSpu smallSpu) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        ValidatorUtils.validateEntity(smallSpu);
        smallSpu.setShopId(user.getShopMan().getShopId());
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
     * 商品库商品保存
     * @return
     */
    @ApiOperation(value="更新商品库商品", notes="")
    @RequestMapping(value = "/updateSpu",method = {RequestMethod.GET})
    public R updateSpu(@LoginUser WxUser user,@RequestBody SmallSpu smallSpu) {
        if(smallSpu.getId()==null){
            return R.error("id不能为空");
        }
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        if(user.getShopMan().getShopId().longValue()!=smallSpu.getShopId().longValue()){
            return R.error("店主所属店铺与操作商品店铺不一致");
        }
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
     * 商品库商品信息
     * @return
     */
    @ApiOperation(value="商品库商品信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/spuInfo",method = {RequestMethod.GET})
    public R spuInfo(@RequestParam Long id,@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        SmallSpu smallSpu = (SmallSpu) smallSpuService.getById(id);
        return R.ok().put("smallSpu",smallSpu);
    }

    /**
     * 商品库商品删除
     * @return
     */
    @ApiOperation(value="商品库商品删除", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delSpu",method = {RequestMethod.GET})
    public R delSpu(@RequestParam Long id,@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        int count =  smallGroupShopService.count(new QueryWrapper<SmallGroupShop>().eq("spu_id",id));
        if(count>0){
            return R.error("有关联团购商品,不能删除");
        }
        boolean result = smallSpuService.removeById(id);
        return result==true?R.ok():R.error("删除失败");
    }


    /**
     * 店主上架商品列表
     * @return
     */
    @ApiOperation(value="店主商商家商品列表", notes="")
    @RequestMapping(value = "/goodsGroupList",method = {RequestMethod.GET})
    public R goodsGroupList(@LoginUser WxUser user,@RequestBody Map<String,Object> params) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        params.put("shopId",user.getShopMan().getShopId());
        params.put("limit", StringUtil.checkObj(params.get("limit"))?params.get("limit"):50);
        Query query = new Query(params);
        PageUtils page = smallGroupShopService.findByPage(query.getPageNum(),query.getPageSize(), query);
        return R.ok().put("list",page.getList());
    }

    /**
     * 保存上架商品(团购商品)
     * @return
     */
    @ApiOperation(value="保存上架商品", notes="")
    @RequestMapping(value = "/saveGroupgoods",method = {RequestMethod.POST})
    public R saveGroupgoods(@LoginUser WxUser user,@RequestBody SmallGroupShop smallGroupShop) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        smallGroupShop.setShopId(user.getShopMan().getShopId());
        Shop shop = (Shop) shopService.getById(smallGroupShop.getShopId());
        //如果是公共商品，设置公共商品id
        if("1".equals(smallGroupShop.getCommonFlag()) && "1".equals(shop.getSysFlag())){
            return R.error("系统店铺不能上架公共商品");
        }
        SmallSpu spu = (SmallSpu) smallSpuService.getById(smallGroupShop.getSpuId());
        smallGroupShop.setSysShopId(spu.getShopId());
        //判断spuId \ skuId\ splierId 是否已存在
        List<SmallGroupShop> list = smallGroupShopService.list(new QueryWrapper<SmallGroupShop>()
                .eq("sku_id",smallGroupShop.getSkuId())
                .eq("spu_id",smallGroupShop.getSpuId())
                .eq("shop_id",smallGroupShop.getShopId()));

        if(list!=null && list.size()>0){
            return R.error("该商品已加入团购列表");
        }
        smallGroupShop.setCreateTime(new Date());
        smallGroupShopService.save(smallGroupShop);

        return R.ok();
    }
    /**
     * 修改
     */
    @ApiOperation(value="修改上架商品", notes="")
    @RequestMapping(value = "/updateGroupgoods",method = {RequestMethod.POST})
    public R updateGroupgoods(@LoginUser WxUser user,@RequestBody SmallGroupShop smallGroupShop){
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        ValidatorUtils.validateEntity(smallGroupShop);
//        smallGroupShop.setShopId(user.getShopMan().getShopId());
        smallGroupShop.setModifyTime(new Date());
        Shop shop = (Shop) shopService.getById(smallGroupShop.getShopId());
        //如果是公共商品，设置公共商品id
        if("1".equals(smallGroupShop.getCommonFlag()) && "1".equals(shop.getSysFlag())){
            return R.error("系统店铺不能上架公共商品");
        }
        SmallSpu spu = (SmallSpu) smallSpuService.getById(smallGroupShop.getSpuId());
        smallGroupShop.setSysShopId(spu.getShopId());
        //判断spuId \ skuId\ splierId 是否已存在
        List<SmallGroupShop> list = smallGroupShopService.list(new QueryWrapper<SmallGroupShop>()
                .eq("sku_id",smallGroupShop.getSkuId())
                .eq("spu_id",smallGroupShop.getSpuId())
                .eq("shop_id",smallGroupShop.getShopId()));
        if(list!=null && list.size()>0 && list.get(0).getId().longValue()!=smallGroupShop.getId().longValue()){
            return R.error("该商品已加入团购列表");
        }
        smallGroupShop.setModifyTime(new Date());
        smallGroupShopService.updateById(smallGroupShop);

        return R.ok();
    }

    /**
     * 上架商品信息
     * @return
     */
    @ApiOperation(value="上架商品信息", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "团购商品id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/groupGoodsInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R groupGoodsInfo(@RequestParam Long id,@LoginUser WxUser user) {
        SmallGroupShop smallGroupShop = (SmallGroupShop)smallGroupShopService.getById(id);
        Shop shop = new Shop();
        if(smallGroupShop.getShopId()!=null){
            shop = (Shop) shopService.getById(smallGroupShop.getShopId());
        }
        smallGroupShop.setShop(shop);
        SmallSku sku = new SmallSku();
        if(smallGroupShop.getSkuId()!=null){
            sku = (SmallSku) smallSkuService.getById(smallGroupShop.getSkuId());
        }
        smallGroupShop.setSku(sku);
        return R.ok().put("smallGroupShop", smallGroupShop);
    }




    /**
     * 删除上架商品
     * @return
     */
    @ApiOperation(value="删除上架商品", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "团购商品id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/delGroupGoods",method = {RequestMethod.GET})
    @ResponseBody
    public R delGroupGoods(@RequestParam Long id,@LoginUser WxUser user) {
        int count =  smallOrderDetailService.count(new QueryWrapper<SmallOrderDetail>().eq("group_id",id));
        if(count>0){
            return R.error("有关联订单,不能删除");
        }
        boolean result = smallGroupShopService.removeById(id);
        return result==true?R.ok():R.error("删除失败");
    }

    /**
     * 批量上下架
     * @return
     */
    @ApiOperation(value="团购商品批量上下架", notes="")
    @RequestMapping(value = "/批量上下架",method = {RequestMethod.POST})
    @ResponseBody
    public R updateSatusBatch(@RequestBody BatchStatusdto batchStatusVo, @LoginUser WxUser user) {
        if(batchStatusVo.getIds().length==0){
            return R.error("ids不能为空");
        }
        if(!"0".equals(batchStatusVo.getStatus()) && "1".equals(batchStatusVo.getStatus())){
            return R.error("状态不正确");
        }
        smallGroupShopService.updateSatusBatch(batchStatusVo);
        return R.ok();
    }
}
