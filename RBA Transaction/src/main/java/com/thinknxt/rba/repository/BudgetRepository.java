package com.thinknxt.rba.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.Budget;
import com.thinknxt.rba.utils.TransactionCategory;

@Generated
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
//    Budget findByAccountNumberAndCategory(Long accountNumber, TransactionCategory category);

    Budget findByAccountNumber(Long accountNumber);
}
