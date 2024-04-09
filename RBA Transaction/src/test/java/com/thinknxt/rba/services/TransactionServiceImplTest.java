package com.thinknxt.rba.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thinknxt.rba.dto.Accounts;
import com.thinknxt.rba.dto.BeneficiaryDTO;
import com.thinknxt.rba.dto.BudgetSetupDTO;
import com.thinknxt.rba.dto.TransactionRequestDTO;
import com.thinknxt.rba.dto.TransferRequestDTO;
import com.thinknxt.rba.entities.Budget;
import com.thinknxt.rba.entities.SavingsTransactionEntity;
import com.thinknxt.rba.entities.TransactionEntity;
import com.thinknxt.rba.exception.AccountDetailsNotFoundException;
import com.thinknxt.rba.mapper.DtoEntityMapper;
import com.thinknxt.rba.repository.BudgetRepository;
import com.thinknxt.rba.repository.CurrentsTransactionRepository;
import com.thinknxt.rba.repository.SavingsTransactionRepository;
import com.thinknxt.rba.repository.TransactionRepository;
import com.thinknxt.rba.response.BudgetSetupResponse;
import com.thinknxt.rba.response.TransactionResponse;
import com.thinknxt.rba.utils.TransactionCategory;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	
	@Mock
	private RestTemplate restTemplate;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private SavingsTransactionRepository savingTransactionRepository;

	@Mock
	private CurrentsTransactionRepository currentsTransactionRepository;

	@InjectMocks
	private TransactionServiceImpl transactionService;

	@Mock
	private DtoEntityMapper dtoEntityMapper;
	
	@Mock
	private BudgetRepository budgetRepository;


	@Test
	void createTransaction_SuccessfulSavingsTransaction_ReturnsSuccessResponse_Savings_Debit() {

		TransactionRequestDTO requestDTO = createTransactionRequestDTODebit();
		Accounts mockAccounts = createMockSavingsAccount();

		ResponseEntity<Void> mockResponseEntity = ResponseEntity.ok().build();

		lenient().when(
				restTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), Mockito.eq(Void.class)))
				.thenReturn(mockResponseEntity);

		when(transactionRepository.save(any())).thenReturn(new TransactionEntity());

		TransactionResponse response = transactionService.createTransaction(requestDTO, mockAccounts);

		assertEquals("Transaction has been successful!!!", response.getMessage());
		assertEquals(200, response.getStatus());
	}

	@Test
	void createTransaction_SuccessfulSavingsTransaction_ReturnsSuccessResponse_Savings_Credit() {

		TransactionRequestDTO requestDTO = createTransactionRequestDTOCredit();
		Accounts mockAccounts = createMockSavingsAccount();

		ResponseEntity<Void> mockResponseEntity = ResponseEntity.ok().build();

		lenient().when(
				restTemplate.postForEntity(Mockito.anyString(), Mockito.any(HttpEntity.class), Mockito.eq(Void.class)))
				.thenReturn(mockResponseEntity);

		when(transactionRepository.save(any())).thenReturn(new TransactionEntity());

		TransactionResponse response = transactionService.createTransaction(requestDTO, mockAccounts);

		assertEquals("Transaction has been successful!!!", response.getMessage());
		assertEquals(200, response.getStatus());
	}

	
	@Test
	void createTransaction_InsufficientFunds_ReturnsErrorResponse_ForSavingsAccount() {

		TransactionRequestDTO requestDTO = new TransactionRequestDTO();

		requestDTO.setAccountNumber(123L);
		requestDTO.setTransactionAmount(50000.0);
		requestDTO.setTransactionType("DEBIT");
		requestDTO.setCustomerId(456);
		requestDTO.setNarratives("Test transaction");

		Accounts mockAccounts = createMockSavingsAccount();

		when(transactionRepository.save(any())).thenReturn(new TransactionEntity());

		TransactionResponse response = transactionService.createTransaction(requestDTO, mockAccounts);
		assertEquals("Insufficient funds!!!", response.getMessage());
		assertEquals(404, response.getStatus());
	}
	
	@Test
	void createTransaction_InsufficientFunds_ReturnsErrorResponse_ForCurrentAccount() {

		TransactionRequestDTO requestDTO = new TransactionRequestDTO();

		requestDTO.setAccountNumber(123L);
		requestDTO.setTransactionAmount(50000.0);
		requestDTO.setTransactionType("DEBIT");
		requestDTO.setCustomerId(456);
		requestDTO.setNarratives("Test transaction");

		Accounts mockAccounts = createMockInvalidAccount();

		when(transactionRepository.save(any())).thenReturn(new TransactionEntity());

		TransactionResponse response = transactionService.createTransaction(requestDTO, mockAccounts);
		assertEquals("Insufficient funds!!!", response.getMessage());
		assertEquals(404, response.getStatus());
	}

	

	@Test
	void createTransaction_SuccessfulSavingsTransaction_ReturnsSuccessResponse_Currents() {

		TransactionRequestDTO requestDTO = createTransactionRequestDTODebit();
		Accounts mockAccounts = createMockInvalidAccount();

		TransactionResponse response = transactionService.createTransaction(requestDTO, mockAccounts);

		assertEquals("Transaction has been successful!!!", response.getMessage());
		assertEquals(200, response.getStatus());
	}

	@Test
	void createTransaction_SuccessfulSavingsTransaction_ReturnsSuccessResponse_Currents_Credit() {

		TransactionRequestDTO requestDTO = createTransactionRequestDTOCredit();
		Accounts mockAccounts = createMockInvalidAccount();

		TransactionResponse response = transactionService.createTransaction(requestDTO, mockAccounts);

		assertEquals("Transaction has been successful!!!", response.getMessage());
		assertEquals(200, response.getStatus());
	}

	private Accounts createMockInvalidAccount() {
		Accounts mockAccounts = new Accounts();
		mockAccounts.setAccounttype("CURRENT");
		mockAccounts.setAccountstatus("ACTIVE");
		mockAccounts.setTotalbalance(1000.0);
		return mockAccounts;
	}
	
	

	
	private TransactionRequestDTO createTransactionRequestDTODebit() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(123L);
		requestDTO.setTransactionAmount(50.0);
		requestDTO.setTransactionType("DEBIT");
		requestDTO.setCustomerId(456);
		requestDTO.setNarratives("Test transaction");
		return requestDTO;
	}

	private TransactionRequestDTO createTransactionRequestDTOCredit() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(123L);
		requestDTO.setTransactionAmount(500.0);
		requestDTO.setTransactionType("CREDIT");
		requestDTO.setCustomerId(456);
		requestDTO.setNarratives("Test transaction");
		return requestDTO;
	}

	private Accounts createMockSavingsAccount() {
		Accounts mockAccounts = new Accounts();
		mockAccounts.setAccounttype("SAVINGS");
		mockAccounts.setAccountstatus("ACTIVE");
		mockAccounts.setTotalbalance(100.0);
		return mockAccounts;
	}

//	@Test
//	public void transferMoneyWithinAccounts_SuccessfulTransfer_savToSav() throws JsonProcessingException {
//		Budget budget = createMockBudget();
//        
//		Accounts accountDetailsResponse = new Accounts();
//
//
//        when(budgetRepository.findByAccountNumber(any(Long.class))).thenReturn(budget);
//
//
//	     TransferRequestDTO transferRequestDTO = createMockTransferRequest();
//
//		accountDetailsResponse = createMockAccounts("ACTIVE");
//
//		ResponseEntity<Accounts> responseEntity = new ResponseEntity<>(accountDetailsResponse,
//				HttpStatus.OK);
//
//		when(dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
//				any(), any(), Mockito.anyDouble(), Mockito.anyInt(), any(), any(), any()))
//                .thenReturn(createMockEntityMap("savToSav"));
//
//		when(restTemplate.getForEntity("http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/1234567890",Accounts.class)).thenReturn(responseEntity);
//
//		ResponseEntity<Void> updateAmountResponseEntity = new ResponseEntity<>(HttpStatus.OK);
//		
//		when(restTemplate.postForEntity("http://localhost:1012/api/retailbanking/accounts/updateAmount?newAmount=950.0&accountNumber=123",
//				null, Void.class)).thenReturn(updateAmountResponseEntity);
//		
//		TransactionResponse result = transactionService.transferMoneyWithinAccounts(transferRequestDTO, 1000000.0, 123,"SAVINGS");
//		assertEquals(200, result.getStatus());
//		assertEquals("Transfer of amount successful to the requested account!!!", result.getMessage());
//	}

	
//	@Test
//	public void transferMoneyWithinAccounts_FailedTransfer_BlockedAccountStatus() throws JsonProcessingException {
//		Accounts accountDetailsResponse = new Accounts();
//		
//	     TransferRequestDTO transferRequestDTO = createMockTransferRequest();
//
//			accountDetailsResponse = createMockAccounts("BLOCKED");
//
//		ResponseEntity<Accounts> responseEntity = new ResponseEntity<>(accountDetailsResponse,
//				HttpStatus.OK);
//
//
//		when(restTemplate.getForEntity("http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/1234567890",Accounts.class)).thenReturn(responseEntity);
//
//		
//		TransactionResponse result = transactionService.transferMoneyWithinAccounts(transferRequestDTO, 1000000.0, 123,"SAVINGS");
//		assertEquals(404, result.getStatus());
//		assertEquals("Transfer request failed as one of the beneficiaries account is blocked for all type of transactions (CREDIT/DEBIT)!!!", result.getMessage());
//	}
	
	
//	@Test
//	public void transferMoneyWithinAccounts_FailedTransfer_CreditBlockedAccountStatus() throws JsonProcessingException {
//		Accounts accountDetailsResponse = new Accounts();
//		
//	     TransferRequestDTO transferRequestDTO = createMockTransferRequest();
//
//			accountDetailsResponse = createMockAccounts("CREDITBLOCKED");
//
//		ResponseEntity<Accounts> responseEntity = new ResponseEntity<>(accountDetailsResponse,
//				HttpStatus.OK);
//
//
//		when(restTemplate.getForEntity("http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/1234567890",Accounts.class)).thenReturn(responseEntity);
//
//		
//		TransactionResponse result = transactionService.transferMoneyWithinAccounts(transferRequestDTO, 1000000.0, 123,"SAVINGS");
//		assertEquals(404, result.getStatus());
//		assertEquals("Transaction Failed!!! The destination account number: 9876543210 is blocked for all CREDIT transactions!!!", result.getMessage());
//	}

	
	
//	@Test
//	public void transferMoneyWithinAccounts_AccountNotFound() {
//		Long beneficiaryAccountNumber = 123456789L;
//        String url = "http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/" + beneficiaryAccountNumber;
//        when(restTemplate.getForEntity(url, Accounts.class))
//            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
//
//        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
//        BeneficiaryDTO beneficiary = new BeneficiaryDTO();
//        beneficiary.setBeneficiaryAccountNumber(beneficiaryAccountNumber);
//        transferRequestDTO.setBeneficiaryName(Collections.singletonList(beneficiary));
//
//       
//        AccountDetailsNotFoundException exception = assertThrows(AccountDetailsNotFoundException.class, () -> {
//        	transactionService.transferMoneyWithinAccounts(transferRequestDTO, 1000, 123, "SAVINGS");
//        });
//        
//        
//        List<Long> expectedNonExistingAccountNumbers = Collections.singletonList(beneficiaryAccountNumber);
//        List<String> actualNonExistingAccountNumbers = exception.getNonExistingAccountNumbers();
//
//        List<Long> longList = actualNonExistingAccountNumbers.stream()
//                .map(Long::valueOf)
//                .collect(Collectors.toList());
//        
//        assertIterableEquals(expectedNonExistingAccountNumbers, longList);
//
//	}

	
	
//	@Test
//	public void transferMoneyWithinAccounts_SuccessfulTransfer_curToCur() throws JsonProcessingException {
//	
//        Budget budget = createMockBudget();
//
//        when(budgetRepository.findByAccountNumber(any(Long.class))).thenReturn(budget);
//
//		Accounts accountDetailsResponse = new Accounts();
//		
//	     TransferRequestDTO transferRequestDTO = createMockTransferRequest();
//
//			accountDetailsResponse = createMockAccounts("ACTIVE");
//
//		ResponseEntity<Accounts> responseEntity = new ResponseEntity<>(accountDetailsResponse,
//				HttpStatus.OK);
//
//		when(dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
//				any(), any(), Mockito.anyDouble(), Mockito.anyInt(), any(), any(), any()))
//                .thenReturn(createMockEntityMap("curToCur"));
//
//		when(restTemplate.getForEntity("http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/1234567890",Accounts.class)).thenReturn(responseEntity);
//
//		ResponseEntity<Void> updateAmountResponseEntity = new ResponseEntity<>(HttpStatus.OK);
//		
//		when(restTemplate.postForEntity("http://localhost:1012/api/retailbanking/accounts/updateAmount?newAmount=950.0&accountNumber=123",
//				null, Void.class)).thenReturn(updateAmountResponseEntity);
//		
//		TransactionResponse result = transactionService.transferMoneyWithinAccounts(transferRequestDTO, 1000000.0, 123,"SAVINGS");
//		assertEquals(200, result.getStatus());
//		assertEquals("Transfer of amount successful to the requested account!!!", result.getMessage());
//	}

	
	
//	@Test
//	public void transferMoneyWithinAccounts_SuccessfulTransfer_savToCur() throws JsonProcessingException {
//
//        Budget budget = createMockBudget();
//        
//		Accounts accountDetailsResponse = new Accounts();
//		
//
//        when(budgetRepository.findByAccountNumber(any(Long.class))).thenReturn(budget);
//
//	    TransferRequestDTO transferRequestDTO = createMockTransferRequest();
//
//		accountDetailsResponse = createMockAccounts("ACTIVE");
//
//		ResponseEntity<Accounts> responseEntity = new ResponseEntity<>(accountDetailsResponse,
//				HttpStatus.OK);
//
//		when(dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
//				any(), any(), Mockito.anyDouble(), Mockito.anyInt(), any(), any(), any()))
//                .thenReturn(createMockEntityMap("savToCur"));
//
//		when(restTemplate.getForEntity("http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/1234567890",Accounts.class)).thenReturn(responseEntity);
//
//		ResponseEntity<Void> updateAmountResponseEntity = new ResponseEntity<>(HttpStatus.OK);
//		
//		when(restTemplate.postForEntity("http://localhost:1012/api/retailbanking/accounts/updateAmount?newAmount=950.0&accountNumber=123",
//				null, Void.class)).thenReturn(updateAmountResponseEntity);
//		
//		TransactionResponse result = transactionService.transferMoneyWithinAccounts(transferRequestDTO, 1000000.0, 123,"SAVINGS");
//		assertEquals(200, result.getStatus());
//		assertEquals("Transfer of amount successful to the requested account!!!", result.getMessage());
//	}
	
	
	
//	@Test
//	public void transferMoneyWithinAccounts_SuccessfulTransfer_curToSav() throws JsonProcessingException {
//Budget budget = createMockBudget();
//        
//		Accounts accountDetailsResponse = new Accounts();
//
//        when(budgetRepository.findByAccountNumber(any(Long.class))).thenReturn(budget);
//
//	     TransferRequestDTO transferRequestDTO = createMockTransferRequest();
//
//			accountDetailsResponse = createMockAccounts("ACTIVE");
//
//		ResponseEntity<Accounts> responseEntity = new ResponseEntity<>(accountDetailsResponse,
//				HttpStatus.OK);
//		
//
//		when(dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
//				any(), any(), Mockito.anyDouble(), Mockito.anyInt(), any(), any(), any()))
//                .thenReturn(createMockEntityMap("curToSav"));
//
//		when(restTemplate.getForEntity("http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/1234567890",Accounts.class)).thenReturn(responseEntity);
//
//		ResponseEntity<Void> updateAmountResponseEntity = new ResponseEntity<>(HttpStatus.OK);
//		
//		when(restTemplate.postForEntity("http://localhost:1012/api/retailbanking/accounts/updateAmount?newAmount=950.0&accountNumber=123",
//				null, Void.class)).thenReturn(updateAmountResponseEntity);
//		
//		TransactionResponse result = transactionService.transferMoneyWithinAccounts(transferRequestDTO, 1000000.0, 123,"SAVINGS");
//		assertEquals(200, result.getStatus());
//		assertEquals("Transfer of amount successful to the requested account!!!", result.getMessage());
//	}
	
	
	
	
	@Test
    public void getBudgetLimitByExpenseCategory_BudgetFound_Returns200() {
        Long accountNumber = 123456789L;
        Budget budget = new Budget();
        String jsonData = "{\"1\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 0.0, \"fuel_threshold\": 0.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 0.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 0.0, \"miscellaneous_threshold\": 0.0}, \"2\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 80.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"3\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"4\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"5\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"6\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"7\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"8\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"9\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"10\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"11\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"12\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}}";
        budget.setAccountNumber(accountNumber);
        budget.setBudgetData(jsonData);
       
        Mockito.when(budgetRepository.findByAccountNumber(accountNumber)).thenReturn(budget);

        Map<String, JsonNode> expectedMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();

        Double thresholdValue = 1000.0; 
        if (thresholdValue != null) {
            jsonNode.put("threshold", thresholdValue);
        }
        expectedMap.put("transactionCategory", jsonNode);
        BudgetSetupResponse response = transactionService.getBudgetLimitByExpenseCategory(accountNumber);

        assertEquals(200, response.getStatus());
        assertEquals("Expense wise budgets found for requested account number!!!", response.getMessage());
    }
	
	
	
		private Map<String, Object> createMockEntityMap(String transferRequestType) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("savingsTransactionEntityForSenderAccount", new SavingsTransactionEntity());
	        map.put("savingsTransactionEntityForBeneficiaryAccount", new SavingsTransactionEntity());
	        map.put("transactionEntity", new TransactionEntity());
	        map.put("transferRequestType", transferRequestType);
	        return map;
	    }
	

		@Test
	    public void updateBudgetLimitByAccountNumber_SuccessfulUpdate() throws Exception {
	        // Mock data
	        Long accountNumber = 123456789L;
	        BudgetSetupDTO budgetSetupDTO = new BudgetSetupDTO();
	        budgetSetupDTO.setMonth(2);
	        
	        Map<TransactionCategory,Double> budgetThreshold = new HashMap<TransactionCategory,Double>();
	        
	        budgetThreshold.put(TransactionCategory.food, 20.0);
	        
	        budgetSetupDTO.setBudgetThreshold(budgetThreshold);
	        

	        Budget budget = createMockBudget();
	        when(budgetRepository.findByAccountNumber(accountNumber)).thenReturn(budget);

	        BudgetSetupResponse response = transactionService.updateBudgetLimitByAccountNumber(accountNumber, budgetSetupDTO);

	        assertEquals(200, response.getStatus());
	        assertEquals("Budgets have been set for the requested expense categories from current month: " + LocalDate.now().getMonth() + ", till end of the year", response.getMessage());
	    }

		
		
		    private Accounts createMockAccounts(String accountStatus) {
		        Accounts sampleAccountsData = new Accounts();
		        sampleAccountsData.setAccountnumber(9876543210L);
		        sampleAccountsData.setTotalbalance(1000000.0);
		        sampleAccountsData.setAccountstatus(accountStatus);
		        sampleAccountsData.setCurrency("INR");
		        sampleAccountsData.setOverdraft("NO");
		        sampleAccountsData.setAccounttype("SAVINGS");
		        sampleAccountsData.setCustomerid(123);
		        return sampleAccountsData;
		    }

		    private TransferRequestDTO createMockTransferRequest() {
		        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
		        transferRequestDTO.setFromAccountNumber(9876543210L);
		        transferRequestDTO.setTransferAmount(50.0);
		        transferRequestDTO.setBeneficiaryName(Collections.singletonList(new BeneficiaryDTO(1234567890L)));
		        return transferRequestDTO;
		    }

		    
		    public static Budget createMockBudget() {
		        Budget budget = new Budget();
		        budget.setAccountNumber(1234567890L);
		        String jsonData = "{\"1\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 0.0, \"fuel_threshold\": 0.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 0.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 0.0, \"miscellaneous_threshold\": 0.0}, \"2\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 80.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"3\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"4\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"5\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"6\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"7\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"8\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"9\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"10\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"11\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}, \"12\": {\"food_expense\": 0.0, \"fuel_expense\": 0.0, \"loan_expense\": 0.0, \"bills_expense\": 0.0, \"food_threshold\": 1000.0, \"fuel_threshold\": 2000.0, \"loan_threshold\": 0.0, \"travel_expense\": 0.0, \"bills_threshold\": 0.0, \"recharge_expense\": 0.0, \"shopping_expense\": 0.0, \"travel_threshold\": 0.0, \"recharge_threshold\": 0.0, \"shopping_threshold\": 5000.0, \"entertainment_expense\": 0.0, \"miscellaneous_expense\": 0.0, \"entertainment_threshold\": 1500.0, \"miscellaneous_threshold\": 100.0}}";

		        budget.setBudgetData(jsonData);
		        return budget;
		    }		
		
		
		
	/*Test case for service finding all active account*/
    @Mock
    private Logger log;
 
    
    @Test
    void testFindAllAccount() {
        // Mock input data
        List<Accounts> inputAccounts = Arrays.asList(
                new Accounts(),
                new Accounts(),
                new Accounts()
        );
 
        // Set data for the first account
        Accounts firstAccount = inputAccounts.get(0);
        firstAccount.setAccountnumber(1);
        firstAccount.setCustomerid(101);
        firstAccount.setAccounttype("Savings");
        firstAccount.setAccountstatus("Active");
        firstAccount.setCurrency("USD");
        firstAccount.setOverdraft("NO");
        firstAccount.setCreationdate(Date.valueOf("2024-01-01"));
        firstAccount.setTotalbalance(1000.0);
 
        // Set data for the second account
        Accounts secondAccount = inputAccounts.get(1);
        secondAccount.setAccountnumber(2);
        secondAccount.setCustomerid(102);
        secondAccount.setAccounttype("Checking");
        secondAccount.setAccountstatus("Inactive");
        secondAccount.setCurrency("EUR");
        secondAccount.setOverdraft("YES");
        secondAccount.setCreationdate(Date.valueOf("2024-02-01"));
        secondAccount.setTotalbalance(2000.0);
 
        // Set data for the third account
        Accounts thirdAccount = inputAccounts.get(2);
        thirdAccount.setAccountnumber(3);
        thirdAccount.setCustomerid(103);
        thirdAccount.setAccounttype("Savings");
        thirdAccount.setAccountstatus("Active");
        thirdAccount.setCurrency("GBP");
        thirdAccount.setOverdraft("NO");
        thirdAccount.setCreationdate(Date.valueOf("2024-03-01"));
        thirdAccount.setTotalbalance(3000.0);
 
        // Call the method being tested
        List<Accounts> result = transactionService.findAllAccount(inputAccounts);
 
        // Assert that the result matches the expected active accounts
        assertEquals(2, result.size()); // Assuming two accounts are active in the mock data
        // Add more specific assertions based on your actual data and filtering logic
    }
	
}