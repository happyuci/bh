package com.sfwl.bh.config;

import cn.jsms.api.common.SMSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    @Value("${jpush.message.appkey}")
    private String appkey;
    @Value("${jpush.message.masterSecret}")
    private String masterSecret;

    @Bean
    public SMSClient sMSClient() {
        return new SMSClient(masterSecret, appkey);
    }

}
