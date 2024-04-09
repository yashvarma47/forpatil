package com.thinknxt.rba.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thinknxt.rba.entities.CurrentsTransactionEntity;
import com.thinknxt.rba.repository.CurrentsTransactionRepository;
import com.thinknxt.rba.response.TransactionDTOResponse;

@ExtendWith(MockitoExtension.class)
class CurrentsTransactionServiceImplTest {

    @Mock
    CurrentsTransactionRepository currentsTransactionRepository;

    @InjectMocks
    CurrentsTransactionServiceImpl currentsTransactionService;

  

    @Test
    void testGetLatestTransactionsByAccountNumber_InvalidAccountType() {
        // Assertions
        assertThrows(RuntimeException.class, () -> currentsTransactionService.getLatestTransactionsByAccountNumber(123L, "savings"));
    }
    
  
    @Test
    void testGetMonthlyTransactionsByAccountNumber_InvalidAccountType() {
        // Assertions
        assertThrows(RuntimeException.class, () -> currentsTransactionService.getMonthlyTransactionsByAccountNumber(123L, "savings", 2024, 2));
    }

  

    @Test
    void testGetQuarterlyTransactionsByAccountNumber_InvalidAccountType() {
        // Assertions
        assertThrows(RuntimeException.class, () -> currentsTransactionService.getQuarterlyTransactionsByAccountNumber(123L, "savings", 2024, 1));
    }

   

    @Test
    void testGetYearlyTransactionsByAccountNumber_InvalidAccountType() {
        // Assertions
        assertThrows(RuntimeException.class, () -> currentsTransactionService.getYearlyTransactionsByAccountNumber(123L, "savings", 2024));
    }


  
}




    
    

