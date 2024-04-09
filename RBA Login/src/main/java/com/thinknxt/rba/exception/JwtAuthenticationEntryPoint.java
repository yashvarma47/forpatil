package com.thinknxt.rba.exception;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.thinknxt.rba.config.Generated;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
@Generated
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (authException instanceof JwtAuthenticationException) {
            handleJwtAuthenticationException(response, (JwtAuthenticationException) authException);
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
        }
    }

    private void handleJwtAuthenticationException(HttpServletResponse response, JwtAuthenticationException ex) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

	@Override
	public void commence(jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException)
			throws IOException, jakarta.servlet.ServletException {
	
	}
}
