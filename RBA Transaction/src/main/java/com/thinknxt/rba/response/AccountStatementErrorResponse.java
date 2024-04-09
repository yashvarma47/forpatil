package com.thinknxt.rba.response;
 
import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
 
@Data
@AllArgsConstructor
@Generated
public class AccountStatementErrorResponse {
	private int status;
	private String message;
	
}