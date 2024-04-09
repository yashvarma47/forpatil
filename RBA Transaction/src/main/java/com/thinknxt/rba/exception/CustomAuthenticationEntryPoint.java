package com.thinknxt.rba.exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.response.ErrorResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Generated
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    
    private void handleJwtAuthenticationException(HttpServletResponse response,
                                                  JwtAuthenticationException jwtAuthException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
       response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString("Customized Unauthorized Response"));
    }

	@Override
	public void commence(jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException)
			throws IOException, jakarta.servlet.ServletException {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),authException.getMessage(),null);	    
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	
	}
}
