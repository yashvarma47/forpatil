package com.thinknxt.rba.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinknxt.rba.dto.Accounts;
import com.thinknxt.rba.dto.BeneficiaryDTO;
import com.thinknxt.rba.dto.BudgetSetupDTO;
import com.thinknxt.rba.dto.TransactionRequestDTO;
import com.thinknxt.rba.dto.TransferRequestDTO;
import com.thinknxt.rba.dto.TransferRequestInSelfAccountDTO;
import com.thinknxt.rba.response.BudgetSetupResponse;
import com.thinknxt.rba.response.ExpenseCategoryResponse;
import com.thinknxt.rba.response.FindAccountResponce;
import com.thinknxt.rba.response.TransactionResponse;
import com.thinknxt.rba.services.CurrentsTransactionServiceImpl;
import com.thinknxt.rba.services.SavingsTransactionServiceImpl;
import com.thinknxt.rba.services.TransactionService;
import com.thinknxt.rba.utils.DayTransaction;

@ExtendWith(MockitoExtension.class)
public class TransactionsControllerTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private TransactionService transactionService;

	@InjectMocks
	private TransactionsController transactionsController;

	@Mock
	private CurrentsTransactionServiceImpl currentsTransactionService;

	@Mock
	private SavingsTransactionServiceImpl savingsTransactionService;

	@Test
	void createTransactionSuccess() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(1234567890L);
		requestDTO.setTransactionType("Credit");
		requestDTO.setCustomerId(123);

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100000.0);
		sampleAccountsData.setAccountstatus("ACTIVE");


		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenReturn(new ResponseEntity<>(sampleAccountsData, HttpStatus.OK));

		TransactionResponse successResponse = new TransactionResponse();
		successResponse.setStatus(200);
		successResponse.setMessage("Transaction created successfully");

		Mockito.when(transactionService.createTransaction(Mockito.eq(requestDTO),
				Mockito.eq(sampleAccountsData))).thenReturn(successResponse);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController.createTransaction(requestDTO);
		Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assertions.assertEquals(successResponse, responseEntity.getBody());
	}

	@Test
	void createTransactionFailure_ResponseEmpty() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(1234567890L);
		requestDTO.setTransactionType("Credit");
		requestDTO.setCustomerId(123);

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100000.0);
		sampleAccountsData.setAccountstatus("ACTIVE");

		
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenReturn(new ResponseEntity<>(sampleAccountsData, HttpStatus.OK));

		TransactionResponse errorResponse = null;
		Mockito.when(transactionService.createTransaction(Mockito.eq(requestDTO),
				Mockito.eq(sampleAccountsData))).thenReturn(errorResponse);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController.createTransaction(requestDTO);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assertions.assertEquals(errorResponse, responseEntity.getBody());
	}

	@Test
	void createTransactionFailure_AllTransactionTypeBlocked() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(1234567890L);
		requestDTO.setTransactionType("Credit");

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100000.0);
		sampleAccountsData.setAccountstatus("BLOCKED");

		
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenReturn(new ResponseEntity<>(sampleAccountsData, HttpStatus.OK));

		TransactionResponse errorResponse = new TransactionResponse();
		errorResponse.setStatus(404);
		errorResponse.setMessage("Transaction Failed!!! The account number: " + requestDTO.getAccountNumber()
				+ " is blocked for all type of transactions (CREDIT/DEBIT)!!!");
		errorResponse.setFaultCode("ERR-RBA-100C&DB");

		lenient().when(transactionService.createTransaction(Mockito.eq(requestDTO),
				Mockito.eq(sampleAccountsData))).thenReturn(errorResponse);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController.createTransaction(requestDTO);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assertions.assertEquals(errorResponse, responseEntity.getBody());
	}

	@Test
	void createTransactionFailure_CreditBlocked() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(1234567890L);
		requestDTO.setTransactionType("Credit");

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100000.0);
		sampleAccountsData.setAccountstatus("CREDITBLOCKED");

	
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenReturn(new ResponseEntity<>(sampleAccountsData, HttpStatus.OK));

		TransactionResponse errorResponse = new TransactionResponse();
		errorResponse.setStatus(404);
		errorResponse.setMessage("Transaction Failed!!! The account number: " + requestDTO.getAccountNumber()
				+ " is blocked for all CREDIT transactions!!!");
		errorResponse.setFaultCode("ERR-RBA-100CB");

		lenient().when(transactionService.createTransaction(Mockito.eq(requestDTO),
				Mockito.eq(sampleAccountsData))).thenReturn(errorResponse);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController.createTransaction(requestDTO);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assertions.assertEquals(errorResponse, responseEntity.getBody());
	}

	@Test
	void createTransactionFailure_DebitBlocked() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(1234567890L);
		requestDTO.setTransactionType("Debit");

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100000.0);
		sampleAccountsData.setAccountstatus("DEBITBLOCKED");

		
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenReturn(new ResponseEntity<>(sampleAccountsData, HttpStatus.OK));

		TransactionResponse errorResponse = new TransactionResponse();
		errorResponse.setStatus(404);
		errorResponse.setMessage("Transaction Failed!!! The account number: " + requestDTO.getAccountNumber()
				+ " is blocked for all DEBIT transactions!!!");
		errorResponse.setFaultCode("ERR-RBA-100DB");

		lenient().when(transactionService.createTransaction(Mockito.eq(requestDTO),
				Mockito.eq(sampleAccountsData))).thenReturn(errorResponse);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController.createTransaction(requestDTO);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assertions.assertEquals(errorResponse, responseEntity.getBody());
	}

	@Test
	void createTransactionAccountNotFound() {
		TransactionRequestDTO requestDTO = new TransactionRequestDTO();
		requestDTO.setAccountNumber(1234567890L);

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Account details not found"));

		ResponseEntity<TransactionResponse> responseEntity = transactionsController.createTransaction(requestDTO);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assertions.assertEquals(404, responseEntity.getBody().getStatus());
		Assertions.assertEquals("Account details not found", responseEntity.getBody().getMessage());
	}

	@Test
	void testTransferMoneyWithinAccounts_AccountNotFound() throws JsonProcessingException {

		TransferRequestDTO requestDTO = new TransferRequestDTO();
		requestDTO.setFromAccountNumber(123L);
		requestDTO.setTransactionNote("Payment for Food");
		requestDTO.setTransferAmount(1000.0);

		List<BeneficiaryDTO> beneficiaryName = new ArrayList<BeneficiaryDTO>();
		BeneficiaryDTO beneficiaryDTO = new BeneficiaryDTO();
		beneficiaryDTO.setBeneficiaryAccountNumber(1234L);
		beneficiaryName.add(beneficiaryDTO);
		requestDTO.setBeneficiaryName(beneficiaryName);

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Account details not found"));

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinAccounts(requestDTO);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assertions.assertEquals(404, responseEntity.getBody().getStatus());
		Assertions.assertEquals("Account details not found: " + requestDTO.getFromAccountNumber(),
				responseEntity.getBody().getMessage());
	}

	@Test
	void testTransferMoneyWithinSelfAccounts_AccountNotFound() throws JsonProcessingException {

		TransferRequestInSelfAccountDTO requestDTO = new TransferRequestInSelfAccountDTO();
		requestDTO.setTransferAmount(1000.0);
		requestDTO.setTransactionNote("Payment for food");
		requestDTO.setToAccountNumber(123761246L);
		requestDTO.setFromAccountNumber(9876543210L);

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Accounts.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Account details not found"));

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinSelfAccounts(requestDTO);

		Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assertions.assertEquals(404, responseEntity.getBody().getStatus());
		Assertions.assertEquals("Account details not found", responseEntity.getBody().getMessage());
	}

	@Test
	public void testTransferMoneyWithinAccounts_Success() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100000.0);
		sampleAccountsData.setAccountstatus("ACTIVE");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);


		when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse());

		TransferRequestDTO requestDTO = new TransferRequestDTO();
		requestDTO.setFromAccountNumber(123L);
		requestDTO.setTransactionNote("Payment for Food");
		requestDTO.setTransferAmount(1000.0);

		List<BeneficiaryDTO> beneficiaryName = new ArrayList<BeneficiaryDTO>();
		BeneficiaryDTO beneficiaryDTO = new BeneficiaryDTO();
		beneficiaryDTO.setBeneficiaryAccountNumber(1234L);
		beneficiaryName.add(beneficiaryDTO);
		requestDTO.setBeneficiaryName(beneficiaryName);
		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinAccounts(requestDTO);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testTransferMoneyWithinSelfAccounts_Success() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100000.0);
		sampleAccountsData.setAccountstatus("ACTIVE");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);

		
		when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse());

		TransferRequestInSelfAccountDTO requestDTO = new TransferRequestInSelfAccountDTO();
		requestDTO.setTransferAmount(1000.0);
		requestDTO.setTransactionNote("Payment for food");
		requestDTO.setToAccountNumber(123761246L);
		requestDTO.setFromAccountNumber(9876543210L);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinSelfAccounts(requestDTO);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testTransferMoneyWithinAccounts_InsufficientFunds() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100.0);
		sampleAccountsData.setAccountstatus("ACTIVE");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);

		
		when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		lenient().when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse(null,
						"Transaction Failed!!! The sender's account number: \"+sampleAccountsData.getAccountnumber()+\" has insufficient funds to perform transaction!!!",
						400, "ERR-RBA-1001"));

		TransferRequestDTO requestDTO = new TransferRequestDTO();
		requestDTO.setBeneficiaryName(List.of(new BeneficiaryDTO(1234567890L)));
		requestDTO.setTransferAmount(1000.0);
		requestDTO.setFromAccountNumber(9876543210L);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinAccounts(requestDTO);

		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals(400, responseEntity.getBody().getStatus());

		assertEquals("Transaction Failed!!! The sender's account number: " + sampleAccountsData.getAccountnumber()
				+ " has insufficient funds to perform transaction!!!", responseEntity.getBody().getMessage());
		assertEquals("ERR-RBA-1001-SEND", responseEntity.getBody().getFaultCode());
	}

	@Test
	public void testTransferMoneyWithinSelfAccounts_InsufficientFunds() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(100.0);
		sampleAccountsData.setAccountstatus("ACTIVE");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);

		
		when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		lenient().when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse(null,
						"Transaction Failed!!! The sender's account number: \"+sampleAccountsData.getAccountnumber()+\" has insufficient funds to perform transaction!!!",
						400, "ERR-RBA-1001"));

		TransferRequestInSelfAccountDTO requestDTO = new TransferRequestInSelfAccountDTO();
		requestDTO.setTransferAmount(10000000.0);
		requestDTO.setTransactionNote("Payment for food");
		requestDTO.setToAccountNumber(123761246L);
		requestDTO.setFromAccountNumber(9876543210L);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinSelfAccounts(requestDTO);

		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals(400, responseEntity.getBody().getStatus());

		assertEquals("Transaction Failed!!! The sender's account number: " + sampleAccountsData.getAccountnumber()
				+ " has insufficient funds to perform transaction!!!", responseEntity.getBody().getMessage());
		assertEquals("ERR-RBA-1001-SEND", responseEntity.getBody().getFaultCode());
	}

	@Test
	public void testTransferMoneyWithinSelfAccounts_DebitBlock() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(1000000.0);
		sampleAccountsData.setAccountstatus("DEBITBLOCKED");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);

		when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		lenient().when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse(null,
						"Transaction Failed!!! The sender's account number: \"+sampleAccountsData.getAccountnumber()+\" is blocked for all DEBIT transactions!!!",
						400, "ERR-RBA-100DB"));

		TransferRequestInSelfAccountDTO requestDTO = new TransferRequestInSelfAccountDTO();
		requestDTO.setTransferAmount(10000000.0);
		requestDTO.setTransactionNote("Payment for food");
		requestDTO.setToAccountNumber(123761246L);
		requestDTO.setFromAccountNumber(9876543210L);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinSelfAccounts(requestDTO);

		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals(400, responseEntity.getBody().getStatus());

		assertEquals("ERR-RBA-100DB-SEND", responseEntity.getBody().getFaultCode());
	}

	@Test
	public void testTransferMoneyWithinSelfAccounts_AllTransactionBlocked() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(1000000.0);
		sampleAccountsData.setAccountstatus("BLOCKED");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);

				when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		lenient().when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse(null,
						"Transaction Failed!!! The sender's account number: \"+sampleAccountsData.getAccountnumber()+\" is blocked for all type of transactions!!!",
						400, "ERR-RBA-100C&DB"));

		TransferRequestInSelfAccountDTO requestDTO = new TransferRequestInSelfAccountDTO();
		requestDTO.setTransferAmount(10000000.0);
		requestDTO.setTransactionNote("Payment for food");
		requestDTO.setToAccountNumber(123761246L);
		requestDTO.setFromAccountNumber(9876543210L);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinSelfAccounts(requestDTO);


		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals(400, responseEntity.getBody().getStatus());
		assertEquals("ERR-RBA-100C&DB-SEND", responseEntity.getBody().getFaultCode());
	}

	@Test
	public void testTransferMoneyWithinAccounts_AllTransactionBlocked() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(1000000.0);
		sampleAccountsData.setAccountstatus("BLOCKED");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);

		
		when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		lenient().when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse(null,
						"Transaction Failed!!! The sender's account number: \"+sampleAccountsData.getAccountnumber()+\" is blocked for all type of transactions!!!",
						400, "ERR-RBA-100C&DB"));

		TransferRequestDTO requestDTO = new TransferRequestDTO();
		requestDTO.setBeneficiaryName(List.of(new BeneficiaryDTO(1234567890L)));
		requestDTO.setTransferAmount(1000.0);
		requestDTO.setFromAccountNumber(9876543210L);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinAccounts(requestDTO);


		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals(400, responseEntity.getBody().getStatus());
		assertEquals("ERR-RBA-100C&DB-SEND", responseEntity.getBody().getFaultCode());
	}

	@Test
	public void testTransferMoneyWithinAccounts_DebitBlock() throws JsonProcessingException {

		Accounts sampleAccountsData = new Accounts();
		sampleAccountsData.setAccountnumber(9876543210L);
		sampleAccountsData.setTotalbalance(1000000.0);
		sampleAccountsData.setAccountstatus("DEBITBLOCKED");
		sampleAccountsData.setCurrency("INR");
		sampleAccountsData.setOverdraft("NO");
		sampleAccountsData.setAccounttype("SAVINGS");
		sampleAccountsData.setCustomerid(123);

		
		
		when(restTemplate.getForEntity(anyString(), eq(Accounts.class)))
				.thenReturn(ResponseEntity.ok(sampleAccountsData));

		lenient().when(transactionService.transferMoneyWithinAccounts(any(), anyDouble(), anyInt(), anyString()))
				.thenReturn(new TransactionResponse(null,
						"Transaction Failed!!! The sender's account number: \"+sampleAccountsData.getAccountnumber()+\" is blocked for all DEBIT transactions!!!",
						400, "ERR-RBA-100DB"));

		TransferRequestDTO requestDTO = new TransferRequestDTO();
		requestDTO.setBeneficiaryName(List.of(new BeneficiaryDTO(1234567890L)));
		requestDTO.setTransferAmount(1000.0);
		requestDTO.setFromAccountNumber(9876543210L);

		ResponseEntity<TransactionResponse> responseEntity = transactionsController
				.transferMoneyWithinAccounts(requestDTO);

		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals(400, responseEntity.getBody().getStatus());

		assertEquals("ERR-RBA-100DB-SEND", responseEntity.getBody().getFaultCode());
	}




	private FindAccountResponce createMockResponseWithAccounts() {
		Accounts mockAccount1 = new Accounts();
		Accounts mockAccount2 = new Accounts();

		return new FindAccountResponce(Arrays.asList(mockAccount1, mockAccount2), 200,
				"Accounts retrieved successfully");
	}

	private FindAccountResponce createMockResponseWithOneAccount() {
		Accounts mockAccount = new Accounts();

		return new FindAccountResponce(Collections.singletonList(mockAccount), 200, "Accounts retrieved successfully");
	}

	private FindAccountResponce createMockResponseWithNoAccount() {
		return new FindAccountResponce(Collections.emptyList(), 200, "No accounts found");
	}

	private List<Accounts> createMockAccountsList() {
		Accounts mockAccount1 = new Accounts();
		Accounts mockAccount2 = new Accounts();

		return Arrays.asList(mockAccount1, mockAccount2);
	}

//************************************Junit test case for budget api's*********************************

	@Test
	public void testGetYearlyTransactionsByExpenseCategory_Success_Savings() {
		// Setup
		long accountNumber = 123456789;
		String accountType = "savings";
		int year = 2023;
		ExpenseCategoryResponse mockResponse = new ExpenseCategoryResponse(/* mock data */);
		when(savingsTransactionService.getYearlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, year)).thenReturn(mockResponse);

		ResponseEntity<Object> response = transactionsController.getYearlyTransactionsByExpenseCategory(accountNumber,
				accountType, year);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockResponse, response.getBody());
	}

	@Test
	public void testGetYearlyTransactionsByExpenseCategory_Success_Current() {
		long accountNumber = 123456789;
		String accountType = "current";
		int year = 2023;
		ExpenseCategoryResponse mockResponse = new ExpenseCategoryResponse(/* mock data */);

		when(currentsTransactionService.getYearlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, year)).thenReturn(mockResponse);

		ResponseEntity<Object> response = transactionsController.getYearlyTransactionsByExpenseCategory(accountNumber,
				accountType, year);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockResponse, response.getBody());
	}

	@Test
	public void testGetYearlyTransactionsByExpenseCategory_InvalidAccountType() {
		long accountNumber = 123456789;
		String accountType = "invalidType";
		int year = 2023;

		ResponseEntity<Object> response = transactionsController.getYearlyTransactionsByExpenseCategory(accountNumber,
				accountType, year);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testGetYearlyTransactionsByExpenseCategory_NoDataFound() {
		long accountNumber = 123456789;
		String accountType = "savings";
		int year = 2023;
		when(savingsTransactionService.getYearlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, year)).thenReturn(null);

		ResponseEntity<Object> response = transactionsController.getYearlyTransactionsByExpenseCategory(accountNumber,
				accountType, year);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testGetYearlyTransactionsByExpenseCategory_BadRequest() {
		long accountNumber = 123456789;
		String accountType = "savings";
		int year = 2023;
		when(savingsTransactionService.getYearlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, year)).thenThrow(new RuntimeException("Some error occurred"));

		ResponseEntity<Object> response = transactionsController.getYearlyTransactionsByExpenseCategory(accountNumber,
				accountType, year);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	// monthly expense test cases

	@Test
	public void testGetMonthlyTransactionsByExpenseCategory_Success_Savings() {
		long accountNumber = 123456789;
		String accountType = "savings";
		int month = 2;
		ExpenseCategoryResponse mockResponse = new ExpenseCategoryResponse(/* mock data */);
		when(savingsTransactionService.getMonthlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, Year.now().getValue(), month)).thenReturn(mockResponse);

		ResponseEntity<Object> response = transactionsController.getMonthlyTransactionsByExpenseCategory(accountNumber,
				accountType, month);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockResponse, response.getBody());
	}

	@Test
	public void testGetMonthlyTransactionsByExpenseCategory_Success_Currents() {
		long accountNumber = 123456789;
		String accountType = "current";
		int month = 2;
		ExpenseCategoryResponse mockResponse = new ExpenseCategoryResponse(/* mock data */);
		when(currentsTransactionService.getMonthlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, Year.now().getValue(), month)).thenReturn(mockResponse);

		ResponseEntity<Object> response = transactionsController.getMonthlyTransactionsByExpenseCategory(accountNumber,
				accountType, month);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockResponse, response.getBody());
	}

	@Test
	void testGetMonthlyTransactionsByExpenseCategory_InvalidAccountType() {
		long accountNumber = 123456789;
		String invalidAccountType = "invalidType";
		int month = 2;

		ResponseEntity<Object> responseEntity = transactionsController
				.getMonthlyTransactionsByExpenseCategory(accountNumber, invalidAccountType, month);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}

	@Test
	public void testGetMonthlyTransactionsByExpenseCategory_NoDataFound() {
		long accountNumber = 123456789;
		String accountType = "savings";
		int month = 2;
		when(savingsTransactionService.getMonthlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, Year.now().getValue(), month)).thenReturn(null);

		ResponseEntity<Object> response = transactionsController.getMonthlyTransactionsByExpenseCategory(accountNumber,
				accountType, month);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testGetMonthlyTransactionsByExpenseCategory_BadRequest() {
		long accountNumber = 123456789;
		String accountType = "savings";
		int month = 2;
		when(savingsTransactionService.getMonthlyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType, Year.now().getValue(), month)).thenThrow(new RuntimeException("Some error occurred"));

		ResponseEntity<Object> response = transactionsController.getMonthlyTransactionsByExpenseCategory(accountNumber,
				accountType, month);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	// weekly

	@Test
	public void testGetWeeklyTransactionsByExpenseCategory_Success_Savings() {
		long accountNumber = 123456789;
		String accountType = "savings";
		ExpenseCategoryResponse mockResponse = new ExpenseCategoryResponse(/* mock data */);
		when(savingsTransactionService.getWeeklyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType)).thenReturn(mockResponse);

		ResponseEntity<Object> response = transactionsController.getWeeklyTransactionsByExpenseCategory(accountNumber,
				accountType);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockResponse, response.getBody());
	}

	@Test
	public void testGetWeeklyTransactionsByExpenseCategory_Success_Currents() {
		long accountNumber = 123456789;
		String accountType = "current";
		ExpenseCategoryResponse mockResponse = new ExpenseCategoryResponse(/* mock data */);
		when(currentsTransactionService.getWeeklyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType)).thenReturn(mockResponse);

		ResponseEntity<Object> response = transactionsController.getWeeklyTransactionsByExpenseCategory(accountNumber,
				accountType);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(mockResponse, response.getBody());
	}

	@Test
	public void testGetWeeklyTransactionsByExpenseCategory_InvalidAccountType() {
		long accountNumber = 123456789;
		String accountType = "invalidType";

		ResponseEntity<Object> response = transactionsController.getWeeklyTransactionsByExpenseCategory(accountNumber,
				accountType);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testGetWeeklyTransactionsByExpenseCategory_NoDataFound() {
		long accountNumber = 123456789;
		String accountType = "savings";
		when(savingsTransactionService.getWeeklyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType)).thenReturn(null);

		ResponseEntity<Object> response = transactionsController.getWeeklyTransactionsByExpenseCategory(accountNumber,
				accountType);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testGetWeeklyTransactionsByExpenseCategory_BadRequest() {
		long accountNumber = 123456789;
		String accountType = "savings";
		when(savingsTransactionService.getWeeklyTransactionsByAccountNumberAndExpenseCategory(accountNumber,
				accountType)).thenThrow(new RuntimeException("Some error occurred"));

		ResponseEntity<Object> response = transactionsController.getWeeklyTransactionsByExpenseCategory(accountNumber,
				accountType);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	// yesterday,todays

	@Test
	void testGetYesterdaysTransactionsByExpenseCategory_InvalidAccountType() {
		long accountNumber = 123456789;
		String invalidAccountType = "invalidType";
		DayTransaction date = DayTransaction.yesterday;

		ResponseEntity<Object> responseEntity = transactionsController
				.getYesterdaysTransactionsByExpenseCategory(accountNumber, invalidAccountType, date);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}

	@Test
	void testGetYesterdaysTransactionsByExpenseCategory_ValidAccountTypeYesterday() {
		long accountNumber = 123456789;
		String accountType = "savings";
		DayTransaction date = DayTransaction.yesterday;

		ResponseEntity<Object> responseEntity = transactionsController
				.getYesterdaysTransactionsByExpenseCategory(accountNumber, accountType, date);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	void testGetYesterdaysTransactionsByExpenseCategory_ValidAccountTypeToday() {
		long accountNumber = 123456789;
		String accountType = "current";
		DayTransaction date = DayTransaction.today;

		ResponseEntity<Object> responseEntity = transactionsController
				.getYesterdaysTransactionsByExpenseCategory(accountNumber, accountType, date);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	// updatebudget

	@Test
	void testUpdateBudgetLimitByAccountNumber_ValidInput() {
		long accountNumber = 123456789;
		BudgetSetupDTO budgetSetupDTO = new BudgetSetupDTO();
		BudgetSetupResponse expectedResponse = new BudgetSetupResponse(/* initialize your expected response here */);
		when(transactionService.updateBudgetLimitByAccountNumber(Mockito.anyLong(), any(BudgetSetupDTO.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController
				.updateBudgetLimitByAccountNumber(accountNumber, budgetSetupDTO);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
	}

	@Test
	void testUpdateBudgetLimitByAccountNumber_NullDTO() {
		long accountNumber = 123456789;

		ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController
				.updateBudgetLimitByAccountNumber(accountNumber, null);

		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
	}

	@Test
	void testUpdateBudgetLimitByAccountNumber_InvalidAccountNumber() {
		long invalidAccountNumber = -123456789;
		BudgetSetupDTO budgetSetupDTO = new BudgetSetupDTO();

		ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController
				.updateBudgetLimitByAccountNumber(invalidAccountNumber, budgetSetupDTO);

		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
	}

	@Test
	void testUpdateBudgetLimitByAccountNumber_NotFound() {
		long accountNumber = 123456789;
		BudgetSetupDTO budgetSetupDTO = new BudgetSetupDTO();
		when(transactionService.updateBudgetLimitByAccountNumber(Mockito.anyLong(), any())).thenReturn(null);
		ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController
				.updateBudgetLimitByAccountNumber(accountNumber, budgetSetupDTO);

		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}

//get budget

	@Test
    void testGetBudgetLimitByAccountNumber_AccountNumberNotGreaterThanZero() {
        long accountNumber = 0; 
        ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController.getBudgetLimitByAccountNumber(accountNumber);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
	
	
	@Test
    void testGetBudgetLimitByAccountNumber_BudgetNotFound() {
        long accountNumber = 123456789;
        when(transactionService.getBudgetLimitByExpenseCategory(accountNumber)).thenReturn(null);

        ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController.getBudgetLimitByAccountNumber(accountNumber);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
	
	
	
	@Test
    void testGetBudgetLimitByAccountNumber_BudgetFound() {
        long accountNumber = 123456789;
        BudgetSetupResponse expectedResponse = new BudgetSetupResponse(200, "Success", new BudgetSetupDTO());
        when(transactionService.getBudgetLimitByExpenseCategory(accountNumber)).thenReturn(expectedResponse);

        ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController.getBudgetLimitByAccountNumber(accountNumber);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }
	
	
	@Test
    void updateBudgetLimitByAccountNumber_InternalServerError() {
        long accountNumber = 123456789;
        String errorMessage = "Internal Server Error";
        BudgetSetupDTO budgetSetupDTO = new BudgetSetupDTO();
        
        when(transactionService.updateBudgetLimitByAccountNumber(accountNumber,budgetSetupDTO)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController.updateBudgetLimitByAccountNumber(accountNumber,budgetSetupDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
	
	@Test
    void testGetBudgetLimitByAccountNumber_InternalServerError() {
        long accountNumber = 123456789;
        String errorMessage = "Internal Server Error";
        when(transactionService.getBudgetLimitByExpenseCategory(accountNumber)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<BudgetSetupResponse> responseEntity = transactionsController.getBudgetLimitByAccountNumber(accountNumber);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}