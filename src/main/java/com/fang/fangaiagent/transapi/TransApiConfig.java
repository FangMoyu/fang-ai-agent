package com.fang.fangaiagent.transapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransApiConfig {
    @Value("${baidu.appid}")
    private String appid;

    @Value("${baidu.securityKey}")
    private String securityKey;

    @Bean
    public TransApi transApi() {
        return new TransApi(this.appid, this.securityKey);
   }

}
