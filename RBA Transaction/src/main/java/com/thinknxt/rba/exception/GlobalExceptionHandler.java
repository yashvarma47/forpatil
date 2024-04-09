package com.thinknxt.rba.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.response.ErrorDetail;
import com.thinknxt.rba.response.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

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

	
	 @ExceptionHandler(ConstraintViolationException.class)
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
	        int status = HttpStatus.BAD_REQUEST.value();
	        String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

	        List<ErrorDetail> details = ex.getConstraintViolations().stream()
	                .map(constraintViolation -> new ErrorDetail(
	                        constraintViolation.getPropertyPath().toString(),
	                        constraintViolation.getMessage()))
	                .collect(Collectors.toList());

	        ErrorResponse errorResponse = new ErrorResponse(status, error, details);
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	    }
	 
	 
	 
	 @ExceptionHandler(HttpMessageNotReadableException.class)
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
	        int status = HttpStatus.BAD_REQUEST.value();
	        String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

	        ErrorDetail errorDetail = new ErrorDetail("message", "Malformed JSON request");
	        ErrorResponse errorResponse = new ErrorResponse(status, error, Collections.singletonList(errorDetail));

	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	    }	 
	 
	 @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
	        int status = HttpStatus.METHOD_NOT_ALLOWED.value();
	        String error = HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase();
	        String message = "Supported methods are: " + String.join(", ", ex.getSupportedMethods());
	        ErrorDetail errorDetail = new ErrorDetail("method", message);
	        ErrorResponse errorResponse = new ErrorResponse(status, error, Collections.singletonList(errorDetail));

	        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	    }

	 @ExceptionHandler(NoHandlerFoundException.class)
	    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
	            NoHandlerFoundException ex) {
	        int status = HttpStatus.NOT_FOUND.value();
	        String error = HttpStatus.NOT_FOUND.getReasonPhrase();

	        ErrorDetail errorDetail = new ErrorDetail("resource", "Invalid URL : Resource not found");
	        ErrorResponse errorResponse = new ErrorResponse(status, error, Collections.singletonList(errorDetail));

	        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
	    }
	 
	 
	 
	 
	 
	 
	 
	 
//	@ExceptionHandler(HttpMessageNotReadableException.class)
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex,
//			HttpServletRequest resquest) {
//		int status = HttpStatus.BAD_REQUEST.value();
//		String error = HttpStatus.BAD_REQUEST.getReasonPhrase();
//		List<ErrorDetail> details = new ArrayList<ErrorDetail>();
//		details.add(new ErrorDetail("dateOfBirth", "Date format is not valid"));
//
//		ErrorResponse errorResponse = new ErrorResponse(status, error, details);
//		return ResponseEntity.badRequest().body(errorResponse);
//	}
	 
	 
	 @ExceptionHandler(MethodArgumentNotValidException.class)
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
	        int status = HttpStatus.BAD_REQUEST.value();
	        String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

	        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
	                .map(fieldError -> new ErrorDetail(
	                        fieldError.getField(),
	                        fieldError.getDefaultMessage()))
	                .collect(Collectors.toList());

	        ErrorResponse errorResponse = new ErrorResponse(status, error, details);
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	    }

	
	 
	 @ExceptionHandler(AccountDetailsNotFoundException.class)
	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountDetailsNotFoundException ex) {
	        List<String> nonExistingAccountNumbers = ex.getNonExistingAccountNumbers();
	       
	        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
	                nonExistingAccountNumbers.stream()
	                        .map(accountNumber -> new ErrorDetail("accountNumber", "Account not found: " + accountNumber))
	                        .toList());
	        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	    }	
}
