package com.thinknxt.rba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import com.thinknxt.rba.config.Generated;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(exclude= {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = "com.thinknxt.rba")
@Slf4j
@Generated
@EnableDiscoveryClient
public class SpringSecurityApplication {

	public static void main(String[] args) {
		log.info("Starting SpringSecurityApplication in RBA Customer...");
		SpringApplication.run(SpringSecurityApplication.class, args);
	}

}
