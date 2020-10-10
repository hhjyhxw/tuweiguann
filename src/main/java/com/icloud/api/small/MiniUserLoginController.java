package com.icloud.api.small;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.extra.emoji.EmojiUtil;
import com.alibaba.fastjson.JSONObject;
import com.icloud.annotation.AuthIgnore;
import com.icloud.annotation.LoginUser;
import com.icloud.api.vo.MiniUserLoginVo;
import com.icloud.basecommon.service.redis.RedisService;
import com.icloud.common.IpUtil;
import com.icloud.common.R;
import com.icloud.common.ShaEncry;
import com.icloud.common.util.StringUtil;
import com.icloud.common.validator.ValidatorUtils;
import com.icloud.config.global.mini.WxMaConfiguration;
import com.icloud.config.global.wx.utils.JsonUtils;
import com.icloud.exceptions.ApiException;
import com.icloud.modules.wx.entity.WxUser;
import com.icloud.modules.wx.service.WxUserService;
import com.icloud.modules.wx.vo.UserVo;
import com.icloud.xcx.util.AES;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidAlgorithmParameterException;
import java.util.Date;
import java.util.List;

@Api("用户登陆接口")
@RestController
//@RequestMapping("/api/wxuser/{appid}")
@RequestMapping("/api/wxuser")
public class MiniUserLoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private RedisService redisService;


    /**
     * 微信登陆接口
     */
    @AuthIgnore
    @ApiOperation(value="小程序登录", notes="")
    @RequestMapping(value = "/login",method = {RequestMethod.POST})
    @ResponseBody
    public R login(@RequestBody MiniUserLoginVo miniUserLoginVo) {
        ValidatorUtils.validateEntityForFront(miniUserLoginVo);
//        if (StringUtils.isBlank(code))return R.error("code不能为空");
//        if (StringUtils.isBlank(signature))return R.error("signature不能为空");
//        if (StringUtils.isBlank(rawData))return R.error("rawData不能为空");
//        if (StringUtils.isBlank(iv))return R.error("iv不能为空");

        final WxMaService wxService = WxMaConfiguration.getMaService(miniUserLoginVo.getAppid());
        try {
            //根据code获取用户openid 和 SessionKey
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(miniUserLoginVo.getCode());
            this.logger.info(session.getSessionKey());
            this.logger.info(session.getOpenid());
            if(StringUtil.checkStr(session.getOpenid()) && StringUtil.checkStr(session.getSessionKey())){
                //验证签名
                String newsignature = ShaEncry.getSha1(miniUserLoginVo.getRawData()+session.getSessionKey());
                if(!newsignature.equals(miniUserLoginVo.getSignature())){
                    return R.error("签名错误");
                }
                //解密数据
                String decryData = AES.decrypt(miniUserLoginVo.getEncryptedData(), session.getSessionKey(), miniUserLoginVo.getIv());
                logger.info("decryData:" + decryData);
                //解密后的用户数据
                JSONObject decryDataJson = JSONObject.parseObject(decryData);
                WxUser user = wxUserService.findByXcxopenid(session.getOpenid());
                if(user!=null){
                    user.setNickname(decryDataJson.containsKey("nickName")? EmojiUtil.toAlias(decryDataJson.getString("nickName")):user.getNickname());
                    user.setHeadimgurl(decryDataJson.containsKey("avatarUrl")?decryDataJson.getString("avatarUrl"):user.getHeadimgurl());
                    user.setModifyTime(new Date());
                    user.setUnionid(decryDataJson.containsKey("unionId")?decryDataJson.getString("unionId"):user.getUnionid());

                    user.setLastLoginTime(user.getCreateTime());
                    user.setLastLoginIp(IpUtil.getIpAddr(httpServletRequest));
                    wxUserService.updateById(user);
                    user.setStatus("1");
//                    user.setXcxopenid(session.getOpenid());
                    user.setLoginType("1");
                }else{
                    user = new WxUser();
                    user.setModifyTime(new Date());
                    user.setNickname(decryDataJson.containsKey("nickName")? EmojiUtil.toAlias(decryDataJson.getString("nickName")):user.getNickname());
                    user.setHeadimgurl(decryDataJson.containsKey("avatarUrl")?decryDataJson.getString("avatarUrl"):user.getHeadimgurl());
                    user.setModifyTime(new Date());
                    user.setUnionid(decryDataJson.containsKey("unionId")?decryDataJson.getString("unionId"):user.getUnionid());
                    user.setXcxopenid(session.getOpenid());
                    user.setCreateTime(new Date());
                    user.setLastLoginTime(user.getCreateTime());
                    user.setLastLoginIp(IpUtil.getIpAddr(httpServletRequest));
                    user.setStatus("1");
                    wxUserService.save(user);
                }
                String accessToken = new RandomGenerator(12).generate();
                user.setToken(accessToken);
                redisService.set(accessToken,user,3000L);//兼容h5、APP 前端服务 登陆
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user,userVo);
                userVo.setAccessToken(user.getToken());
                return R.ok().put("user",userVo);
            }else{
                return R.error("小程序登录失败");
            }
        } catch (WxErrorException | InvalidAlgorithmParameterException e) {
            this.logger.error(e.getMessage(), e);
            return R.error(e.toString());
        }
    }

//    /**
//     * 微信登陆接口
//     */
//    @ApiOperation(value="账号登陆", notes="")
//    @RequestMapping(value = "/loginByAccount",method = {RequestMethod.POST})
//    @ResponseBody
//    @AuthIgnore
//    public R loginByAccount(@RequestBody UserAccount userAccount) throws ApiException {
//        ValidatorUtils.validateEntityForFront(userAccount);
//        List<CardVerifyuser> list = cardVerifyuserService.list(new QueryWrapper<CardVerifyuser>().eq("account",userAccount.getAccount()));
//        if(list==null && list.size()==0){
//            return R.error("账号不存在");
//        }
//        CardVerifyuser cardVerifyuser = list.get(0);
//        if(cardVerifyuser.getPassword()!=null && !MD5Utils.encode2hex(userAccount.getPassword()).equals(cardVerifyuser.getPassword())){
//            return R.error("密码错误");
//        }
//        if(cardVerifyuser.getUserId()==null){
//            return R.error("该账号未绑定");
//        }
//        WxUser user = (WxUser) wxUserService.getById(cardVerifyuser.getUserId());
//        String accessToken = new RandomGenerator(12).generate();
//        user.setToken(accessToken);
//        redisService.set(accessToken,user,3000L);//兼容h5、APP 前端服务 登陆
//        UserVo userVo = new UserVo();
//        BeanUtils.copyProperties(user,userVo);
//        userVo.setAccessToken(user.getToken());
//        return R.ok().put("user",userVo);
//    }

    @ApiOperation(value="小程序退出登录", notes="")
    @RequestMapping(value = "/logout",method = {RequestMethod.POST})
    @ResponseBody
    public R logout(@LoginUser WxUser user) throws ApiException {
        redisService.remove(httpServletRequest.getHeader("accessToken"));
        return new R().ok("ok");
    }







    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
//    @GetMapping("/phone")
    public String phone(@PathVariable String appid, String sessionKey, String signature,
                        String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        return JsonUtils.toJson(phoneNoInfo);
    }
}
