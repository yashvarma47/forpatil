package com.thinknxt.rba.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Generated
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
    	log.info("Creating BCryptPasswordEncoder bean...");
        return new BCryptPasswordEncoder();
    }
}
