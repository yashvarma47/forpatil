package com.thinknxt.rba.exception;
import java.io.IOException;
import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.thinknxt.rba.config.Generated;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Generated
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		log.info("Inside commence function of class JwtAuthenticationEntryPoint  ");
        if (authException instanceof JwtAuthenticationException) {
        	log.info("Inside if part of commence function of class JwtAuthenticationEntryPoint  ");
            handleJwtAuthenticationException(response, (JwtAuthenticationException) authException);
        } else {
        	log.info("Inside else part of commence function of class JwtAuthenticationEntryPoint  ");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
        }
    }

    private void handleJwtAuthenticationException(HttpServletResponse response, JwtAuthenticationException ex) throws IOException {
    	log.info("Inside handleJwtAuthenticationException function of class JwtAuthenticationEntryPoint  ");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

//	@Override
//	public void commence(jakarta.servlet.http.HttpServletRequest request,
//			jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException)
//			throws IOException, jakarta.servlet.ServletException {
//	
//	}
}
