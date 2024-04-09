package com.thinknxt.rba.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.response.ErrorDetail;
import com.thinknxt.rba.response.ErrorResponse;

@RestControllerAdvice
@Generated
public class GlobalExceptionHandler {

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleValidationException(BindException ex) {
		int status = HttpStatus.BAD_REQUEST.value();
		String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

		List<ErrorDetail> details = new ArrayList<>();
		ex.getFieldErrors().forEach((errorField) -> {
			String field = errorField.getField();
			String errorMessage = errorField.getDefaultMessage();
			details.add(new ErrorDetail(field, errorMessage));
		});

		ErrorResponse errorResponse = new ErrorResponse(status, error, details);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);

		});
		return errors;
	}
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(JwtAuthenticationException.class)
	public ResponseEntity<String> InvalidJwtTokenException(JwtAuthenticationException ex) {
		Map<String, String> errors = new HashMap<>();
		String fieldName = "JWT_TOKEN";
		String errorMessage = ex.getLocalizedMessage();	
		errors.put(fieldName, errorMessage);
		return ResponseEntity.badRequest().body(errorMessage);
	}	
}
