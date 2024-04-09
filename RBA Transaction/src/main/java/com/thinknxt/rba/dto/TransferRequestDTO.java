package com.thinknxt.rba.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.utils.TransactionCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data	
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class TransferRequestDTO {

	@NotNull(message = "From account number cannot be null")
    private Long fromAccountNumber;

    @NotEmpty(message = "Beneficiary list cannot be empty")
    @Valid
    private List<@Valid BeneficiaryDTO> beneficiaryName;

    @Positive(message = "Transfer amount must be positive")
    private double transferAmount;
    
    @Size(min = 5, max = 255)
    private String transactionNote;
    
//    @NotNull(message = "Transaction Category cannot be null")
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
