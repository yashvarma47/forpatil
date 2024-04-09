package com.thinknxt.rba.services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thinknxt.rba.entities.LoginDetails;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private RestTemplate restTemplate;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		HttpHeaders headers = new HttpHeaders();

		String jwtToken = request.getHeader("Authorization");
		
		headers.set("Authorization", jwtToken);
		
		final String API_URL = "http://localhost:1010/api/retailbanking/login/fetchLoginDetails/" + username;

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<LoginDetails> loginDetails = restTemplate.exchange(API_URL, HttpMethod.GET, entity,LoginDetails.class);

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

		authorities.add(new SimpleGrantedAuthority(loginDetails.getBody().getRole()));

		return new org.springframework.security.core.userdetails.User(username, loginDetails.getBody().getPassword(),
				authorities);

	}
}
