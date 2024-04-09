package com.thinknxt.rba.response;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class ErrorDetail {

	private String field;
	private String message;
}
