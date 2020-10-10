package com.icloud.config.global.wx;

import com.icloud.config.global.wx.utils.JsonUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * wechat mp properties
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Data
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayProperties {

//    private  MpConfig configs;

//    @Data
//    public static class MpConfig {
//        private String mchid;
//        private String mchkey;
//        private String mchkeyV3;
//        private String notifyurl;
//        private String keypath;
//    }

    private String mchid;
    private String mchkey;
    private String mchkeyV3;
    private String notifyurl;
    private String keypath;

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
