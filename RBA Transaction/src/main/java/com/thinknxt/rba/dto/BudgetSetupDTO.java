package com.thinknxt.rba.dto;
 
import java.time.LocalDate;
import java.util.Map;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.utils.TransactionCategory;
 
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@Generated
@AllArgsConstructor
public class BudgetSetupDTO {
 
    @Positive(message = "Month must be a positive integer")
    private int month = LocalDate.now().getMonthValue();
	
    @Valid
    @NotNull(message = "Budget threshold map cannot be null")
    @Size(min = 1, message = "At least one budget threshold must be provided")
	private Map<TransactionCategory,Double> budgetThreshold;	
}