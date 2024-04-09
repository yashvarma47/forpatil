package com.thinknxt.rba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.thinknxt.rba.config.Generated;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@Generated
@EnableDiscoveryClient
public class SpringSecurityApplication {

	public static void main(String[] args) {
		log.info("Starting SpringSecurityApplication in RBA Login");
		SpringApplication.run(SpringSecurityApplication.class, args);
	}
}
