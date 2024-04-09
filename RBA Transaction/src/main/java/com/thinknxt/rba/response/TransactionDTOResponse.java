package com.thinknxt.rba.response;

import java.time.LocalDateTime;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.utils.TransactionCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class TransactionDTOResponse {

    private Long id;
    private Integer customerId;
    private Long accountNumber;
    private String transactionId;
    private Double transactionAmount;
    private String transactionType;
    private Double remainingBalance;
    private LocalDateTime transactionTime;
    private Long recipient;
    private String narratives;
    private TransactionCategory transactionCategory;

}