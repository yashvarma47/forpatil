package com.thinknxt.rba.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.utils.TransactionCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data	
@AllArgsConstructor
@Generated
@NoArgsConstructor
public class TransferRequestInSelfAccountDTO {

	@NotNull
	private Long fromAccountNumber;
	
	@NotNull
    private Long toAccountNumber;
	
	@Positive
    private double transferAmount;
	
	@NotBlank
    private String transactionNote;
	
//	@NotNull(message = "Transaction Category cannot be null")
    private TransactionCategory transactionCategory;
	
	public void setTransferAmount(double transferAmount) {
        BigDecimal amountBigDecimal = BigDecimal.valueOf(transferAmount);
        this.transferAmount = amountBigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
	
	
	 public void setTransactionCategory(TransactionCategory transactionCategory) {
	        if (transactionCategory == null) {
	            this.transactionCategory = TransactionCategory.miscellaneous;
	        } else {
	            this.transactionCategory = transactionCategory;
	        }
	    }

}
