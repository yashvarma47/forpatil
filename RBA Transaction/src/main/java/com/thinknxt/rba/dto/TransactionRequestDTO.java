package com.thinknxt.rba.dto;

import com.thinknxt.rba.config.Generated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Generated
@NoArgsConstructor
public class TransactionRequestDTO {

	@Min(value = 1, message = "Customer ID must be greater than or equal to 1")
	private int customerId;
	
	@NotNull
	private Long accountNumber;
	
	@Positive
	private double transactionAmount;
	
	@NotBlank
	private String transactionType;

	@Size(min = 5, max = 255)
	private String narratives;
}
