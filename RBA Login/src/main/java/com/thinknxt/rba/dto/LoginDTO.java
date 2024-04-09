package com.thinknxt.rba.dto;

import com.thinknxt.rba.config.Generated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class LoginDTO {

//	@Size(min = 8, max = 8, message = "Customer ID should be of 8 characters only!!!")
	@Min(value = 10000000, message = "Customer ID must be an 8-digit number")
    @Max(value = 99999999, message = "Customer ID must be an 8-digit number")
	private int userId;
	
	@NotNull(message = "Password Field must not be null")
	private String password;
	
}
