package com.thinknxt.rba.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.response.ErrorDetail;
import com.thinknxt.rba.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@Generated
public class GlobalExceptionHandler {

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleValidationException(BindException ex) {
		log.info("Inside handleValidationException function of class GlobalExceptionHandler  ");
		int status = HttpStatus.BAD_REQUEST.value();
		String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

		List<ErrorDetail> details = new ArrayList<>();
		ex.getFieldErrors().forEach((errorField) -> {
			String field = errorField.getField();
			String errorMessage = errorField.getDefaultMessage();
			details.add(new ErrorDetail(field, errorMessage));
		});

		ErrorResponse errorResponse = new ErrorResponse(status, error, details);
		log.info("end of handleValidationException function of class GlobalExceptionHandler  ");
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex,
			HttpServletRequest resquest) {
		log.info("Inside handleJsonParseException function of class GlobalExceptionHandler  ");
		int status = HttpStatus.BAD_REQUEST.value();
		String error = HttpStatus.BAD_REQUEST.getReasonPhrase();
		List<ErrorDetail> details = new ArrayList<>();

		// You can customize the error message based on your requirements
		details.add(new ErrorDetail("dateOfBirth", "Date format is not valid"));

		ErrorResponse errorResponse = new ErrorResponse(status, error, details);
		log.info("End handleJsonParseException function of class GlobalExceptionHandler  ");
		return ResponseEntity.badRequest().body(errorResponse);
	}
}
