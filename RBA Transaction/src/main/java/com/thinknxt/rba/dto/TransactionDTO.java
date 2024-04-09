package com.thinknxt.rba.dto;

import java.time.LocalDateTime;

import com.thinknxt.rba.config.Generated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data	
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class TransactionDTO {

	private String transactionId;
    private String transactionStatus;
    private String transactionMode;
    private LocalDateTime transactionTime;	
}
