package com.icloud.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.common.SnowflakeUtils;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.config.ServerConfig;
import com.icloud.exceptions.ApiException;
import com.icloud.exceptions.BaseException;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.entity.ShopTradeDetails;
import com.icloud.modules.small.vo.CreateTradeDetailsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.shop.dao.ShopTradeDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 店铺账号交易明细 
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-09-17 16:07:50
 */
@Service
@Transactional
public class ShopTradeDetailsService extends BaseServiceImpl<ShopTradeDetailsMapper,ShopTradeDetails> {

    @Autowired
    private ShopTradeDetailsMapper shopTradeDetailsMapper;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ServerConfig serverConfig;

    @Override
    public PageUtils<ShopTradeDetails> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<ShopTradeDetails> list = shopTradeDetailsMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<ShopTradeDetails> pageInfo = new PageInfo<ShopTradeDetails>(list);
        PageUtils<ShopTradeDetails> page = new PageUtils<ShopTradeDetails>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    /**
     * 1、添加店铺账户流失
     * 2、更新店铺余额
     * @param vo
     */
    public void createShopTradeDetails(CreateTradeDetailsVo vo) {
        ValidatorUtils.validateEntityForFront(vo);
        if(vo.getInOrOut().intValue()==1 || vo.getInOrOut().intValue()==2){
            //保存店铺账户流失
            ShopTradeDetails tradeDetails = new ShopTradeDetails();
            Shop shop = (Shop) shopService.getById(vo.getShopId());
            BigDecimal shopbalance = shop.getBalance();
            shopbalance = shopbalance!=null?shopbalance:new BigDecimal(0);
            BigDecimal newshopbalance =  new BigDecimal(0);
            tradeDetails.setAmount(vo.getAmount());
            tradeDetails.setBeforeBlance(shopbalance != null ? shopbalance : new BigDecimal(0));
            tradeDetails.setBizType(vo.getBizType());///* 交易类型 7、零售采购收入 8、佣金收入 9、公共订单收入（自营部分 10：自营订单收入，11：账号充值，20：账号提现，21：扣除订单手续费 */
            tradeDetails.setInOrOut(vo.getInOrOut());//1收入 2支出
            if(vo.getInOrOut().intValue()==1){
                tradeDetails.setAfterBlance(tradeDetails.getBeforeBlance().add(vo.getAmount()));
                newshopbalance = shopbalance.add(vo.getAmount());
            }else{
                tradeDetails.setAfterBlance(tradeDetails.getBeforeBlance().subtract(vo.getAmount()));
                newshopbalance = shopbalance.subtract(vo.getAmount());
            }
            tradeDetails.setCreatedTime(new Date());
            tradeDetails.setTradeNo("B" + SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort() % 31L));
            tradeDetails.setOrderNo(vo.getOrderNo());
            tradeDetails.setShopId(shop.getId());
            tradeDetails.setCreatedBy(vo.getUpdateBy());
            int result = shopTradeDetailsMapper.insert(tradeDetails);
            if(result==0){
               throw new ApiException("保存账户流失失败");
            }
            //更新店铺余额
            Shop updateShop = new Shop();
            updateShop.setBalance(newshopbalance);
            updateShop.setUpdatedBy(vo.getUpdateBy());
            updateShop.setUpdatedTime(new Date());
            //根据Id和旧的余额更新 账户新的余额
            if(!(shopService.update(updateShop,new UpdateWrapper<Shop>()
                    .eq("id",shop.getId())
                    .eq("balance",shopbalance)))){
                throw new BaseException("更新账户余额失败");
            }
        }
    }
}

