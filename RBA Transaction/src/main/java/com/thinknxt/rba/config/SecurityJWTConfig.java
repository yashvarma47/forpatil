package com.thinknxt.rba.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinknxt.rba.exception.CustomAccessDeniedHandler;
import com.thinknxt.rba.exception.CustomAuthenticationEntryPoint;
//import com.thinknxt.rba.exception.JwtAuthenticationEntryPoint;
import com.thinknxt.rba.filter.JwtFilter;
import com.thinknxt.rba.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity(prePostEnabled = true)
@Generated
public class SecurityJWTConfig{

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
    private ObjectMapper objectMapper;
	
	@Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	  @Bean
	    public AccessDeniedHandler accessDeniedHandler() {
	        return new CustomAccessDeniedHandler(objectMapper);
	    }
	  
	  @Bean
	    public AuthenticationEntryPoint authenticationEntryPoint() {
	        return new CustomAuthenticationEntryPoint(objectMapper);
	    }
	  
	  
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		 http.csrf().disable().authorizeHttpRequests().requestMatchers("/transactions/**","/generatetoken", "/userId", "/customer/delete-account/{customerId}",
				"/login", "/register-user", "/v3/api-docs/**", "/swagger-ui/**","/v2/api-docs/**","/swagger-resources/**")
				.permitAll().and()
                .authorizeHttpRequests().requestMatchers("/createTransaction").hasAuthority("ADMIN").anyRequest()
                .authenticated()
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler()).authenticationEntryPoint(authenticationEntryPoint()).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
	
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		 http.csrf().disable().authorizeHttpRequests().requestMatchers("/generatetoken", "/userId", "/customer/delete-account/{customerId}",
//				"/login", "/register-user", "/v3/api-docs/**", "/swagger-ui/**","/v2/api-docs/**","/swagger-resources/**")
//				.permitAll().and()
//                .authorizeHttpRequests().requestMatchers("/createTransaction").hasAuthority("ADMIN").anyRequest()
//                .authenticated()
//                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler()).authenticationEntryPoint(authenticationEntryPoint()).and().sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
