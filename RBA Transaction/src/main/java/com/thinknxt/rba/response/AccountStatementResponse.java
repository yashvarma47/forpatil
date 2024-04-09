package com.thinknxt.rba.response;
 
import java.util.List;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
 
@Data
@AllArgsConstructor
@Generated
public class AccountStatementResponse {
	private int status;
	private String message;
	private List<TransactionDTOResponse> data;
	
}