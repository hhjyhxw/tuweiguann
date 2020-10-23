package com.icloud.modules.small.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.binarywang.wxpay.bean.entpay.EntPayBankQueryResult;
import com.github.binarywang.wxpay.bean.entpay.EntPayBankRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayBankResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icloud.basecommon.service.LockComponent;
import com.icloud.common.MapEntryUtils;
import com.icloud.common.PageUtils;
import com.icloud.common.PayUtil;
import com.icloud.common.SnowflakeUtils;
import com.icloud.config.ServerConfig;
import com.icloud.exceptions.ApiException;
import com.icloud.exceptions.BaseException;
import com.icloud.modules.shop.entity.Shop;
import com.icloud.modules.shop.entity.ShopBank;
import com.icloud.modules.shop.service.ShopBankService;
import com.icloud.modules.shop.service.ShopService;
import com.icloud.modules.shop.service.ShopTradeDetailsService;
import com.icloud.modules.small.entity.SmallOrder;
import com.icloud.modules.small.entity.SmallWasteRecord;
import com.icloud.modules.small.vo.CreateTradeDetailsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.icloud.basecommon.service.BaseServiceImpl;
import com.icloud.modules.small.dao.SmallWasteRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 资金流水记录表
 * @author zdh
 * @email yyyyyy@cm.com
 * @date 2020-10-07 20:18:12
 */
@Service
@Transactional
public class SmallWasteRecordService extends BaseServiceImpl<SmallWasteRecordMapper,SmallWasteRecord> {

    @Autowired
    private SmallWasteRecordMapper smallWasteRecordMapper;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopBankService shopBankService;
    @Autowired
    private LockComponent lockComponent;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private ShopTradeDetailsService shopTradeDetailsService;

    //提现锁
    private static final String WASTE_DRAW_LOCK = "WASTE_DRAW_LOCK_LOCK";
    @Override
    public PageUtils<SmallWasteRecord> findByPage(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<SmallWasteRecord> list = smallWasteRecordMapper.queryMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallWasteRecord> pageInfo = new PageInfo<SmallWasteRecord>(list);
        PageUtils<SmallWasteRecord> page = new PageUtils<SmallWasteRecord>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }

    public PageUtils<SmallWasteRecord> queryShenhelistMixList(int pageNo, int pageSize, Map<String, Object> query) {
        PageHelper.startPage(pageNo, pageSize);
        List<SmallWasteRecord> list = smallWasteRecordMapper.queryShenhelistMixList(MapEntryUtils.clearNullValue(query));
        PageInfo<SmallWasteRecord> pageInfo = new PageInfo<SmallWasteRecord>(list);
        PageUtils<SmallWasteRecord> page = new PageUtils<SmallWasteRecord>(list,(int)pageInfo.getTotal(),pageSize,pageNo);
        return page;
    }
    /**
     * 生成资金流失记录
     */
    public void payWasteRecord(SmallWasteRecord smallWasteRecord) {
        try {
            SmallWasteRecord smallWasteRecordold = smallWasteRecordMapper.selectById(smallWasteRecord.getId());
            Shop shop = (Shop) shopService.getById(smallWasteRecordold.getShopId());
//            //提现收款账户
//            ShopBank shopBank = null;
//            List<ShopBank> shopBanklist = shopBankService.list(new QueryWrapper<ShopBank>().eq("shop_id",shop.getId()).eq("status","1"));
//            if(shopBanklist==null || shopBanklist.size()==0){
//                throw new BaseException("收款账号不存在,提现失败");
//            }
            ShopBank shopBank = (ShopBank) shopBankService.getById(smallWasteRecordold.getBankId());
            //本地账户余额
            BigDecimal shopbanlance = shop.getBalance()!=null?shop.getBalance():new BigDecimal(0);
            if(smallWasteRecordold.getAmount().compareTo(shopbanlance)>0){
                smallWasteRecord.setApproveBy("3");
                smallWasteRecord.setMsg("账号余额不足,提现失败");
                smallWasteRecordMapper.updateById(smallWasteRecordold);
                return;
//                throw new BaseException("账号余额不足,提现失败");
            }

            CreateTradeDetailsVo tradeDetailsVo = new CreateTradeDetailsVo();
            tradeDetailsVo.setAmount(smallWasteRecordold.getAmount());
            tradeDetailsVo.setShopId(shop.getId());
            tradeDetailsVo.setBizType(20);
            tradeDetailsVo.setInOrOut(2);
            tradeDetailsVo.setOrderNo(smallWasteRecordold.getOrderNo());
            tradeDetailsVo.setUpdateBy(smallWasteRecord.getApproveBy());
            //锁定30秒
            if (lockComponent.tryLock(WASTE_DRAW_LOCK + smallWasteRecord.getId().toString(), 30)) {
                //1、记录账号变动流水
                //2、更新店铺余额
                shopTradeDetailsService.createShopTradeDetails(tradeDetailsVo);
                //3、发起提现
                EntPayBankResult result = wxPayService.getEntPayService().payBank(EntPayBankRequest.builder()
                        .bankCode(shopBank.getBankCode())//银行卡所在开户行编号,详见银行编号列表
                        .amount(PayUtil.getFinalmoneyInt(String.valueOf(smallWasteRecordold.getAmount()))) //付款金额：RMB分（支付总额，不含手续费） 注：大于0的整数
                        .encBankNo(shopBank.getCardNo())
                        .encTrueName(shopBank.getUserName())
                        .partnerTradeNo(smallWasteRecordold.getOrderNo())//商户订单号，需保持唯一（只允许数字[0~9]或字母[A~Z]和[a~z]，最短8位，最长32位）
                        .description(smallWasteRecordold.getOrderNo())//企业付款到银行卡付款说明,即订单备注（UTF8编码，允许100个字符以内）
                        .build());
                if(result==null){
                    throw new BaseException("企业付款失败");
                }
                if(!"SUCCESS".equals(result.getReturnCode()) || !"SUCCESS".equals(result.getResultCode())){
                    throw new BaseException("企业付款失败,"+result.getErrCodeDes());
                }
                smallWasteRecord.setTransactionId(result.getPaymentNo());
                smallWasteRecord.setWasteState("1");//付款成功
                smallWasteRecord.setModifyTime(new Date());
                smallWasteRecordMapper.updateById(smallWasteRecord);
            } else {
                throw new BaseException("获取锁失败，请稍后");
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("[提现审核] 异常", e);
            throw new BaseException("[提现审核] 异常");
        } finally {
            lockComponent.release(WASTE_DRAW_LOCK + smallWasteRecord.getId().toString());
        }
    }


}

