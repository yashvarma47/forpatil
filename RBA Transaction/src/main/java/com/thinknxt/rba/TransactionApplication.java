package com.thinknxt.rba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.thinknxt.rba.config.Generated;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableTransactionManagement
@EnableEncryptableProperties
@EnableWebMvc
@OpenAPIDefinition(info= @Info(title="Retail Banking Transaction Service"))
@Generated
@EnableDiscoveryClient
public class TransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}
	
	@Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}
