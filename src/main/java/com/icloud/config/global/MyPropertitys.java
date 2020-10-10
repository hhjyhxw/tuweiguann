package com.icloud.config.global;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ConfigurationProperties(prefix="mypros")//加载自定义属性
@Configuration
public class MyPropertitys {

    //项目路径
    private String service_domain;
    //文件上传目录前缀
    private String uploadpath;
    //判断是在本地调试还是发布服务器
    private String activein;
    //
    private Session session;

    /**
     * spring redis session 相关参数
     */
    @Data
    public static class Session{
        private int timeout;//#spring-session中session过期时间 单位：秒
        private String namespace;//#spring-session中redis命名空间
        private String parentDomainName;// #父域名
        private String cookieName;//#cookie名字

    }
}
