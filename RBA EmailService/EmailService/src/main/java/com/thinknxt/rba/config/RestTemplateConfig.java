package com.thinknxt.rba.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Generated
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
    	log.info("Creating RestTemplate bean in class RestTemplateConfig ...");
        return new RestTemplate();
    }
}
