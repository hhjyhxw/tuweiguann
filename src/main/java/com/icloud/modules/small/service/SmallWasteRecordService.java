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
import com.icloud.common.util.StringUtil;
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
import com.icloud.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    @Autowired
    private SysConfigService sysConfigService;

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
     * 提现审核成功
     * 1、发起企业付款到银行卡
     *      成功：
     *      1、更新提现申请记录
     *      失败：
     *      1、更新提现申请记录
     *      2、回退店铺余额
     *      3、生产资金流水
     */
    public void payWasteRecord(SmallWasteRecord smallWasteRecord) {
            SmallWasteRecord smallWasteRecordold = smallWasteRecordMapper.selectById(smallWasteRecord.getId());
            smallWasteRecordold.setApproveTime(new Date());
            smallWasteRecordold.setApproveBy(smallWasteRecord.getApproveBy());
            ShopBank shopBank = (ShopBank) shopBankService.getById(smallWasteRecordold.getBankId());
             EntPayBankResult result = null;
             try {
                 result = wxPayService.getEntPayService().payBank(EntPayBankRequest.builder()
                         .bankCode(shopBank.getBankCode())//银行卡所在开户行编号,详见银行编号列表
                         .amount(PayUtil.getFinalmoneyInt(String.valueOf(smallWasteRecordold.getAmount()))) //付款金额：RMB分（支付总额，不含手续费） 注：大于0的整数
                         .encBankNo(shopBank.getCardNo())
                         .encTrueName(shopBank.getUserName())
                         .partnerTradeNo(smallWasteRecordold.getOrderNo())//商户订单号，需保持唯一（只允许数字[0~9]或字母[A~Z]和[a~z]，最短8位，最长32位）
                         .description(smallWasteRecordold.getOrderNo())//企业付款到银行卡付款说明,即订单备注（UTF8编码，允许100个字符以内）
                         .build());
             }catch (Exception e){
                 e.printStackTrace();
             }
            log.info(smallWasteRecordold.getOrderNo()+"_提现结果====="+result);

             if(result==null || !"SUCCESS".equals(result.getReturnCode())){
                 smallWasteRecordold.setMsg(result==null?"微信企业付款失败":result.getReturnMsg()+"_"+result.getReturnMsg());
                 wasteFaire(smallWasteRecordold);
                 return;
             }
            if(!"SUCCESS".equals(result.getResultCode())){
                //1、更新失败流水
                //2、回退店铺余额
                //3、生成资金流水
                smallWasteRecordold.setMsg(result.getErrCode()+"_"+result.getErrCodeDes());
                wasteFaire(smallWasteRecordold);
                return;
            }
            //企业付款成功
            smallWasteRecordold.setTransactionId(result.getPaymentNo());
            smallWasteRecordold.setWasteState("1");//付款成功
            smallWasteRecordold.setApproveFlag("2");//审核成功，付款成功
            smallWasteRecordold.setModifyTime(new Date());
            smallWasteRecordMapper.updateById(smallWasteRecordold);

    }

    /**
     * 发起提现
     * 1、生产体验申请记录
     *     体现资金记录: 1、冻结店铺余额
     *                 2、生产资金流水
     *     提现手续费:  1、冻结店铺余额
     *                 2、生产资金流水
     *
     * @param smallWasteRecord
     */
    public void createWaste(SmallWasteRecord smallWasteRecord) {
        //
        smallWasteRecord.setOrderNo("T" + SnowflakeUtils.getOrderNoByWordId(serverConfig.getServerPort() % 31L));
        smallWasteRecord.setApproveFlag("0");//提现申请
        smallWasteRecordMapper.insert(smallWasteRecord);

        //1、
        CreateTradeDetailsVo tradeDetailsVo = new CreateTradeDetailsVo();
        tradeDetailsVo.setAmount(smallWasteRecord.getAmount());
        tradeDetailsVo.setShopId(smallWasteRecord.getShopId());
        tradeDetailsVo.setBizType(20);
        tradeDetailsVo.setInOrOut(2);//账号支出
        tradeDetailsVo.setOrderNo(smallWasteRecord.getOrderNo());
        tradeDetailsVo.setUpdateBy(smallWasteRecord.getCreateBy());
        shopTradeDetailsService.createShopTradeDetails(tradeDetailsVo);

        //2、
        CreateTradeDetailsVo tradeDetailsVo2 = new CreateTradeDetailsVo();
        String withdrawFee = sysConfigService.getValue("withdrawFee");
        BigDecimal fee = null;
        if(StringUtil.checkStr(withdrawFee)){
            fee =  new BigDecimal(withdrawFee);
        }
        if(fee==null || fee.compareTo(new BigDecimal(0))<0){
            //不收手续费
            tradeDetailsVo2.setAmount(new BigDecimal(0));
        }else{
            tradeDetailsVo2.setAmount(smallWasteRecord.getAmount().multiply(fee));
        }
        tradeDetailsVo2.setShopId(smallWasteRecord.getShopId());
        tradeDetailsVo2.setBizType(21);
        tradeDetailsVo2.setInOrOut(2);//账号支出
        tradeDetailsVo2.setOrderNo(smallWasteRecord.getOrderNo());
        tradeDetailsVo2.setUpdateBy(smallWasteRecord.getCreateBy());
        shopTradeDetailsService.createShopTradeDetails(tradeDetailsVo2);

    }


    /**
     * 提现失败处理
     *1、更新提现申请记录
     *2、回退店铺余额
     *3、生产资金流水
     * @param smallWasteRecord
     */
    public void wasteFaire(SmallWasteRecord smallWasteRecord) {
        //
        smallWasteRecord.setApproveFlag("3");//提现失败
        smallWasteRecordMapper.updateById(smallWasteRecord);

        //体现额回退
        CreateTradeDetailsVo tradeDetailsVo = new CreateTradeDetailsVo();
        tradeDetailsVo.setAmount(smallWasteRecord.getAmount());
        tradeDetailsVo.setShopId(smallWasteRecord.getShopId());
        tradeDetailsVo.setBizType(22);//提现失败回退余额
        tradeDetailsVo.setInOrOut(1);//账号收入
        tradeDetailsVo.setOrderNo(smallWasteRecord.getOrderNo());
        tradeDetailsVo.setUpdateBy(smallWasteRecord.getApproveBy());
        shopTradeDetailsService.createShopTradeDetails(tradeDetailsVo);

        //手续费回退
        CreateTradeDetailsVo tradeDetailsVo2 = new CreateTradeDetailsVo();
        String withdrawFee = sysConfigService.getValue("withdrawFee");
        BigDecimal fee = null;
        if(StringUtil.checkStr(withdrawFee)){
            fee =  new BigDecimal(withdrawFee);
        }
        if(fee==null || fee.compareTo(new BigDecimal(0))<0){
            //不收手续费
            tradeDetailsVo2.setAmount(new BigDecimal(0));
        }else{
            tradeDetailsVo2.setAmount(smallWasteRecord.getAmount().multiply(fee));
        }
        tradeDetailsVo2.setShopId(smallWasteRecord.getShopId());
        tradeDetailsVo2.setBizType(22);//提现失败回退余额
        tradeDetailsVo2.setInOrOut(1);//账号收入
        tradeDetailsVo2.setOrderNo(smallWasteRecord.getOrderNo());
        tradeDetailsVo2.setUpdateBy(smallWasteRecord.getApproveBy());
        shopTradeDetailsService.createShopTradeDetails(tradeDetailsVo2);


    }


    /**
     * 所有选中的,状态未 approveFlag=0的记录 状态修改成1
     * @param ids
     * @param username
     */
    public void shenheBatch(Long[] ids, String username) {
        if(ids!=null && ids.length>0){
            List<SmallWasteRecord> recordlist = smallWasteRecordMapper.selectList(new QueryWrapper<SmallWasteRecord>().in("id",ids).eq("approve_flag","0"));
            if(recordlist!=null && recordlist.size()>0){
                SmallWasteRecord record = null;
                for (int i=0;i<recordlist.size();i++){
                    record = recordlist.get(i);
                    record.setApproveBy(username);
                    record.setApproveFlag("1");
                    record.setApproveTime(new Date());
                    record.setModifyTime(record.getApproveTime());
                    smallWasteRecordMapper.updateById(record);
                }
            }
        }
    }

}

