package com.thinknxt.rba.response;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@JsonInclude(Include.NON_NULL)
@Generated
public class CustomerResponse {

	private String message;
	private int status;
	private Customer Data;

}
