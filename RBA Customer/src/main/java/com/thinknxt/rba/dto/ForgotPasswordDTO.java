package com.thinknxt.rba.dto;
 
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinknxt.rba.config.Generated;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
@AllArgsConstructor
@Generated
public class ForgotPasswordDTO {
 
	private int userId;
	private String pan;
	@NotNull(message = "Date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateOfBirth;
	private String phoneNumber;	
}