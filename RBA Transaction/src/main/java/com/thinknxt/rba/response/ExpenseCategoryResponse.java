package com.thinknxt.rba.response;
 
import java.util.List;
import java.util.Map;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor 
@Generated
public class ExpenseCategoryResponse {
	private int status;
	private String message;
	private  Map<String, Object> data;
	
}