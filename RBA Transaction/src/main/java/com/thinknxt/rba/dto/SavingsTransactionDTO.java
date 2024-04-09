package com.thinknxt.rba.dto;
import java.time.LocalDateTime;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data	
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class SavingsTransactionDTO {

    private Long id;
    private int customerId;
    private Long accountNumber;
    private String transactionId;
    private double transactionAmount;
    private String transactionType;
    private double remainingBalance;
    private LocalDateTime transactionTime;

   
}
