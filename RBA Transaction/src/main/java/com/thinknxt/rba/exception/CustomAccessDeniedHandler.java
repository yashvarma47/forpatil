package com.thinknxt.rba.exception;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.response.ErrorResponse;

import jakarta.servlet.http.HttpServletResponse;

@Component
@Generated
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

	@Override
	public void handle(jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException, jakarta.servlet.ServletException {
			ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),accessDeniedException.getMessage(),null);	
	    	response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	        response.setContentType("application/json");
	        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		
	}
}
