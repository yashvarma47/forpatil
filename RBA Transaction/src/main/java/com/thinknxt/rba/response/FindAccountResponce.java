package com.thinknxt.rba.response;
import java.util.List;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.dto.Accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class FindAccountResponce {
	 private List<Accounts> data;
	    private int status;
	    private String message;
}