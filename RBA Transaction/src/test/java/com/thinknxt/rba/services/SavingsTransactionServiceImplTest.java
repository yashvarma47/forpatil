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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.thinknxt.rba.entities.SavingsTransactionEntity;
import com.thinknxt.rba.repository.SavingsTransactionRepository;
import com.thinknxt.rba.response.TransactionDTOResponse;

@SpringBootTest
@ActiveProfiles("test")
public class SavingsTransactionServiceImplTest {

    @Mock
    private SavingsTransactionRepository savingsTransactionRepository;

    @InjectMocks
    private SavingsTransactionServiceImpl savingsTransactionService;

    @Test
    void testGetLatestTransactionsByAccountNumber_Success() {
        // Mocking repository response
        List<SavingsTransactionEntity> transactions = new ArrayList<>();
        transactions.add(new SavingsTransactionEntity());
        when(savingsTransactionRepository.findTop10ByAccountNumber(anyLong())).thenReturn(transactions);

        // Calling the service method
        List<TransactionDTOResponse> result = savingsTransactionService.getLatestTransactionsByAccountNumber(123L, "savings");

        // Assertions
        assertEquals(transactions.size(), result.size());
    }

    @Test
    void testGetMonthlyTransactionsByAccountNumber_Success() {
        // Mocking repository response
        List<SavingsTransactionEntity> transactions = new ArrayList<>();
        transactions.add(new SavingsTransactionEntity());
        when(savingsTransactionRepository.findByAccountNumberAndTransactiontimebetweenOrderByTransactionTimeDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(transactions);

        // Calling the service method
        List<TransactionDTOResponse> result = savingsTransactionService.getMonthlyTransactionsByAccountNumber(123L, "savings", 2024, 1);

        // Assertions
        assertEquals(transactions.size(), result.size());
    }

    @Test
    void testGetMonthlyTransactionsByAccountNumber_InvalidAccountType() {
        // Assertions
        assertThrows(RuntimeException.class, () -> savingsTransactionService.getMonthlyTransactionsByAccountNumber(123L, "currents", 2024, 1));
    }

    @Test
    void testGetQuarterlyTransactionsByAccountNumber_Success() {
        // Mocking repository response
        List<SavingsTransactionEntity> transactions = new ArrayList<>();
        transactions.add(new SavingsTransactionEntity());

        // Mocking the repository method call
        when(savingsTransactionRepository.findByAccountNumberAndTransactiontimebetweenOrderByTransactionTimeDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transactions);

        // Calling the service method
        List<TransactionDTOResponse> result = savingsTransactionService.getQuarterlyTransactionsByAccountNumber(123L, "savings", 2024, 1);

        // Assertions
        assertEquals(3, result.size()); // Ensure that the result contains one transaction
    }

    @Test
    void testGetQuarterlyTransactionsByAccountNumber_InvalidAccountType() {
        // Assertions
        assertThrows(RuntimeException.class, () -> savingsTransactionService.getQuarterlyTransactionsByAccountNumber(123L, "currents", 2024, 1));
    }

    @Test
    void testGetYearlyTransactionsByAccountNumber_Success() {
        // Mocking repository response
        List<SavingsTransactionEntity> transactions = new ArrayList<>();
        transactions.add(new SavingsTransactionEntity());

        // Mocking the repository method call
        when(savingsTransactionRepository.findByAccountNumberAndTransactiontimebetweenOrderByTransactionTimeDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transactions);

        // Calling the service method
        List<TransactionDTOResponse> result = savingsTransactionService.getYearlyTransactionsByAccountNumber(123L, "savings", 2024);

        // Assertions
        assertEquals(12, result.size()); // Ensure that the result contains one transaction
    }

    @Test
    void testGetYearlyTransactionsByAccountNumber_InvalidAccountType() {
        // Assertions
        assertThrows(RuntimeException.class, () -> savingsTransactionService.getYearlyTransactionsByAccountNumber(123L, "currents", 2024));
    }
}
    
    
    
    
    


