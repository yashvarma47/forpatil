package com.thinknxt.rba.exception;

import java.util.List;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.response.TransactionResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Generated
public class ErrorMessage {
	
	private List<TransactionResponse> faults;

	public ErrorMessage(List<TransactionResponse> faults) {
		super();
		this.faults = faults;
	}
   
}
