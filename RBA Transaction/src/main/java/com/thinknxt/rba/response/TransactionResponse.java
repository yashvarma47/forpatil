package com.thinknxt.rba.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.dto.Accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
@JsonInclude(Include.NON_NULL)
public class TransactionResponse {

	private Accounts data;
	private String message;
	private int status;
	private String faultCode;
	
}


