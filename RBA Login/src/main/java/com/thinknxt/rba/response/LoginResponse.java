package com.thinknxt.rba.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.dto.LoginUserInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
@JsonInclude(Include.NON_NULL)
public class LoginResponse {

	private String message;
	private int status;
	
	private LoginUserInfoDTO Data;
	private String jwtToken;
}


