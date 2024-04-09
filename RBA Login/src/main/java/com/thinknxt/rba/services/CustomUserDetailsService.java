package com.thinknxt.rba.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.thinknxt.rba.controllers.LoginController;
import com.thinknxt.rba.entities.LoginDetails;
import com.thinknxt.rba.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Override
	/**
 	 *	It retrieves user details from the repository,
	 *
	 * @param username The username for which user details are to be loaded.
	 * @return UserDetails object containing user information.
	 * @throws UsernameNotFoundException If user details for the given username are not found.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Retrieving user details from the repository based on the provided username
		LoginDetails loginDetails = repository.findByUserId(username);

		// Logging the retrieval of user details
		log.info("User details retrieved for username: {}", username);
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(username,
				loginDetails.getPassword(), new ArrayList<>());
		log.info("UserDetails created successfully for username: {}", username);
		// Returning the UserDetails object
		return userDetails;
	}
}
