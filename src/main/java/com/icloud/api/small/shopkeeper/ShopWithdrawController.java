package com.icloud.api.small.shopkeeper;

import com.icloud.annotation.LoginUser;
import com.icloud.api.vo.shopkeeper.ShopDrawVo;
import com.icloud.basecommon.model.Query;
import com.icloud.common.PageUtils;
import com.icloud.common.R;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.exceptions.BaseException;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.entity.ShopTradeDetails;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.shop.service.ShopTradeDetailsService;
import com.icloud.modules.small.entity.SmallWasteRecord;
import com.icloud.modules.small.service.SmallWasteRecordService;
import com.icloud.modules.sys.service.SysConfigService;
import com.icloud.modules.wx.entity.WxUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api("提现管理")
@RestController
@RequestMapping("/api/shopWithdraw")
public class ShopWithdrawController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SmallWasteRecordService smallWasteRecordService;
    @Autowired
    private ShopTradeDetailsService shopTradeDetailsService;

    /**
     * 店铺余额查询
     * @return
     */
    @ApiOperation(value="店铺余额查询", notes="")
    @RequestMapping(value = "/shopblance",method = {RequestMethod.GET})
    public R shopblance(@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        Shop shop = (Shop) shopService.getById(user.getShopMan().getShopId());
        //计算可提现金额
        String withdrawFee = sysConfigService.getValue("withdrawFee");//手续费率
        BigDecimal fee = null;//手续费
        BigDecimal ableAmount = null;//可提现金额
        if(StringUtil.checkStr(withdrawFee)){
            fee =  new BigDecimal(withdrawFee);
        }
        if(fee==null || fee.compareTo(new BigDecimal(0))<0){
            //不收手续费
            ableAmount = shop.getBalance();
        }else{
            ableAmount = shop.getBalance().subtract(shop.getBalance().multiply(fee));
        }
        return R.ok().put("shop", shop).put("ableAmount",ableAmount).put("withdrawFee",fee!=null?fee:new BigDecimal(0));
    }

    /**
     * 提现申请
     * @return
     */
    @ApiOperation(value="提现申请", notes="")
    @RequestMapping(value = "/applyDraw",method = {RequestMethod.GET})
    @ResponseBody
    public R applyDraw(@LoginUser WxUser user, @RequestBody ShopDrawVo shopDrawVo) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        if(user.getShopMan().getShopId().longValue()!=shopDrawVo.getShopId().longValue()){
            return R.error("店主所属店铺与操作店铺不一致");
        }
        //参数校验
        ValidatorUtils.validateEntity(shopDrawVo);
        Shop shop = (Shop) shopService.getById(user.getShopMan().getShopId());
        SmallWasteRecord smallWasteRecord = new SmallWasteRecord();
        BeanUtils.copyProperties(shopDrawVo,smallWasteRecord);
        smallWasteRecord.setCreateBy(user.getShopMan().getAccountNo());
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
        //发起提现申请
        smallWasteRecordService.createWaste(smallWasteRecord);
        return R.ok();
    }

    /**
     * 提现记录
     * @return
     */
    @ApiOperation(value="提现记录", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页多少记录", required = false, paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/drawList",method = {RequestMethod.GET})
    @ResponseBody
    public R drawList(@LoginUser WxUser user,String pageNum,String pageSize) {
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
        query.put("wasteFlag","2");//提现类型
        PageUtils page = smallWasteRecordService.queryShenhelistMixList(query.getPageNum(),query.getPageSize(), query);
        return R.ok().put("page", page);
    }


    /**
     * 提现记录明细
     * @return
     */
    @ApiOperation(value="提现记录明细", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "提现记录id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/drawDetailInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R drawDetailinfo(@RequestParam Long id,@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        SmallWasteRecord smallWasteRecord = (SmallWasteRecord) smallWasteRecordService.getById(id);
        return R.ok().put("smallWasteRecord",smallWasteRecord);
    }

    /**
     * 资金流水记录
     * @return
     */
    @ApiOperation(value="资金流水记录", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页多少记录", required = false, paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/capitalFlowList",method = {RequestMethod.GET})
    @ResponseBody
    public R capitalFlowList(@LoginUser WxUser user,String pageNum,String pageSize) {
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
        PageUtils page = shopTradeDetailsService.findByPage(query.getPageNum(),query.getPageSize(), query);

        return R.ok().put("page", page);
    }

    /**
     * 资金流水记录明细
     * @return
     */
    @ApiOperation(value="资金流水记录明细", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资金流水记录id", required = true, paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/flowDetailInfo",method = {RequestMethod.GET})
    @ResponseBody
    public R flowDetailInfo(@RequestParam Long id,@LoginUser WxUser user) {
        if(user.getShopMan()==null){
            return R.error("不是店主");
        }else if(user.getShopMan()!=null && "0".equals(user.getShopMan().getStatus())){
            return R.error("店主账号已被禁用");
        }
        ShopTradeDetails shopTradeDetails = (ShopTradeDetails) shopTradeDetailsService.getById(id);
        return R.ok().put("shopTradeDetails",shopTradeDetails);
    }

}
