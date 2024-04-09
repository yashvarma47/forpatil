package com.thinknxt.rba.response;
 
import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class BenificiaryResponse {
	private int status;
	private String message;
}