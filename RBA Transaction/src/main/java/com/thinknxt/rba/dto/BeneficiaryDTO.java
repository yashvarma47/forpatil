package com.thinknxt.rba.dto;


import com.thinknxt.rba.config.Generated;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data	
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class BeneficiaryDTO {

	@NotNull
	private Long beneficiaryAccountNumber;
//    private String beneficiaryName;
//    private String emailId;
}
