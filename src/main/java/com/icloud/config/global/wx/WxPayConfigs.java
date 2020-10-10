package com.icloud.config.global.wx;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rize on 2019/6/9.
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxPayProperties.class)
public class WxPayConfigs {

    private final WxPayProperties properties;


//    @Autowired
//    private RedisService redisService;

    @Bean
    public WxPayConfig wxPayConfig() {
        com.github.binarywang.wxpay.config.WxPayConfig payConfig = new com.github.binarywang.wxpay.config.WxPayConfig();
        payConfig.setMchId(this.properties.getMchid());
        payConfig.setMchKey(this.properties.getMchkey());
        payConfig.setNotifyUrl(this.properties.getNotifyurl());
        payConfig.setKeyPath(this.properties.getKeypath());
        payConfig.setSignType("MD5");
        return payConfig;
    }


    @Bean
    public WxPayService wxPayService(com.github.binarywang.wxpay.config.WxPayConfig payConfig) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
}
