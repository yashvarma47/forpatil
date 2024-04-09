package com.thinknxt.rba.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinknxt.rba.dto.Accounts;
import com.thinknxt.rba.dto.AddBenificiaryDTO;
import com.thinknxt.rba.dto.BeneficiaryDTO;
import com.thinknxt.rba.dto.BudgetSetupDTO;
import com.thinknxt.rba.dto.TransactionRequestDTO;
import com.thinknxt.rba.dto.TransferRequestDTO;
import com.thinknxt.rba.dto.TransferRequestInSelfAccountDTO;
import com.thinknxt.rba.entities.BenificiaryAccount;
import com.thinknxt.rba.response.AccountStatementErrorResponse;
import com.thinknxt.rba.response.AccountStatementResponse;
import com.thinknxt.rba.response.BenificiaryResponse;
import com.thinknxt.rba.response.BudgetSetupResponse;
import com.thinknxt.rba.response.ErrorResponse;
import com.thinknxt.rba.response.ExpenseCategoryResponse;
import com.thinknxt.rba.response.FindAccountResponce;
import com.thinknxt.rba.response.GetBenificiaryResponse;
import com.thinknxt.rba.response.TransactionDTOResponse;
import com.thinknxt.rba.response.TransactionResponse;
import com.thinknxt.rba.services.CurrentsTransactionServiceImpl;
import com.thinknxt.rba.services.SavingsTransactionServiceImpl;
import com.thinknxt.rba.services.TransactionService;
import com.thinknxt.rba.utils.DayTransaction;
import com.thinknxt.rba.utils.TransactionCategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequestMapping("/transactions")
@Slf4j
public class TransactionsController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private SavingsTransactionServiceImpl savingsTransactionService;

	@Autowired
	private CurrentsTransactionServiceImpl currentsTransactionService;

	/**
	 * auth Endpoint to create a new transaction.
	 * 
	 * @author sidheshwars
	 * @param transactionRequestDTO The transaction details provided in the request
	 *                              body.
	 * @return ResponseEntity<TransactionResponse> The response entity containing
	 *         the result of the transaction.
	 */
	@PostMapping("/createTransaction")
//	@PreAuthorize("hasAuthority('ADMIN')")
	@Operation(summary = "Create a new transaction", description = "Endpoint to create a new transaction.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<TransactionResponse> createTransaction(
			@RequestBody @Valid TransactionRequestDTO transactionRequestDTO) {

		Long accountNumber = transactionRequestDTO.getAccountNumber();
		String url = "http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/" + accountNumber;
		log.info("Fetching account details for accountNumber: {}", accountNumber);

		ResponseEntity<Accounts> responseEntity = null;
		TransactionResponse errorResponse = null;

		try {
			responseEntity = restTemplate.getForEntity(url, Accounts.class);
			log.info("Received account details in Transaction controller Class: {}", responseEntity.getBody());

		} catch (HttpClientErrorException notFoundException) {
			errorResponse = new TransactionResponse(null, "Account details not found", 404, "ERR-RBA-1006");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		if (responseEntity.getBody().getAccountstatus().equals("BLOCKED")) {
			errorResponse = new TransactionResponse(null, "Transaction Failed!!! The account number: " + accountNumber
					+ " is blocked for all type of transactions (CREDIT/DEBIT)!!!", 404, "ERR-RBA-100C&DB");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		} else if (responseEntity.getBody().getAccountstatus().equals("CREDITBLOCKED")
				&& transactionRequestDTO.getTransactionType().equals("Credit")) {
			errorResponse = new TransactionResponse(null, "Transaction Failed!!! The account number: " + accountNumber
					+ " is blocked for all CREDIT transactions!!!", 404, "ERR-RBA-100CB");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		} else if (responseEntity.getBody().getAccountstatus().equals("DEBITBLOCKED")
				&& transactionRequestDTO.getTransactionType().equals("Debit")) {
			errorResponse = new TransactionResponse(null, "Transaction Failed!!! The account number: " + accountNumber
					+ " is blocked for all DEBIT transactions!!!", 404, "ERR-RBA-100DB");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		else {
			TransactionResponse transactionResponse = transactionService.createTransaction(transactionRequestDTO,
					responseEntity.getBody());

			if (ObjectUtils.isEmpty(transactionResponse)) {
				log.warn("Transaction response is empty. Returning BAD_REQUEST");

				return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
			} else if (transactionResponse.getStatus() == 200) {
				log.info("Transaction created successfully. Returning OK");
				return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
			} else {
				log.warn("Transaction creation failed. {}", transactionResponse.getMessage());
				return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
			}
		}
	}

	/**
	 * Endpoint to transfer money within from one account to different account
	 * belonging to different customer.
	 *
	 * @author sidheshwars
	 * @param transferRequestDTO The transfer details provided in the request body.
	 * @return ResponseEntity<TransactionResponse> The response entity containing
	 *         the result of the money transfer.
	 * @throws JsonProcessingException
	 */
	@PostMapping("/transferWithinAccounts")
	@Operation(summary = "Create a new transfer request to transfer money within accounts", description = "Endpoint to transfer amount from one account to another account.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Transferred amount to another account successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<TransactionResponse> transferMoneyWithinAccounts(
			@RequestBody @Valid TransferRequestDTO transferRequestDTO) throws JsonProcessingException {

		String url = "http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/"
				+ transferRequestDTO.getFromAccountNumber();

		int beneficiaryCount = transferRequestDTO.getBeneficiaryName().size();
		double totalAmountToTransfer = transferRequestDTO.getTransferAmount() * beneficiaryCount;
		log.info("Total Amount to be transfered; {}", totalAmountToTransfer);
		TransactionResponse transactionResponse = new TransactionResponse();
		log.info("Fetching account details for accountNumber: {}", transferRequestDTO.getFromAccountNumber());
		ResponseEntity<Accounts> responseEntity = null;

		if (transferRequestDTO.getTransactionCategory() != null) {
			log.info("Requested transaction category is : {}", transferRequestDTO.getTransactionCategory());
		} else {
			log.info("Requested transaction category is : {}", TransactionCategory.miscellaneous);
		}

		try {
			responseEntity = restTemplate.getForEntity(url, Accounts.class);
			log.info("Received account details in TransactionController Class: {}", responseEntity.getBody());

		} catch (HttpClientErrorException notFoundException) {
			TransactionResponse errorResponse = new TransactionResponse(null,
					"Account details not found: " + transferRequestDTO.getFromAccountNumber(), 404, "ERR-RBA-1006");
			log.info("Account details not found for : {}", transferRequestDTO.getFromAccountNumber());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		if (responseEntity.getBody() != null && responseEntity.getBody().getAccountstatus().equals("BLOCKED")) {
			transactionResponse.setMessage("Transaction Failed!!! The sender's account number: "
					+ responseEntity.getBody().getAccountnumber() + " is blocked for all type of transactions!!!");
			transactionResponse.setStatus(400);
			transactionResponse.setFaultCode("ERR-RBA-100C&DB-SEND");
			return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
		} else if (responseEntity.getBody() != null
				&& responseEntity.getBody().getAccountstatus().equals("DEBITBLOCKED")) {
			transactionResponse.setMessage("Transaction Failed!!! The sender's account number: "
					+ responseEntity.getBody().getAccountnumber() + " is blocked for all DEBIT transactions!!!");
			transactionResponse.setStatus(400);
			transactionResponse.setFaultCode("ERR-RBA-100DB-SEND");
			return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
		} else if (responseEntity.getBody() != null && responseEntity.getBody().getAccountstatus().equals("ACTIVE")
				|| responseEntity.getBody().getAccountstatus().equals("CREDITBLOCKED")) {

			int senderCustomerId = responseEntity.getBody().getCustomerid();
			double senderTotalBalance = responseEntity.getBody().getTotalbalance();
			String senderAccountType = responseEntity.getBody().getAccounttype();
			if (senderTotalBalance >= totalAmountToTransfer) {

				transactionResponse = transactionService.transferMoneyWithinAccounts(transferRequestDTO,
						senderTotalBalance, senderCustomerId, senderAccountType); // service method call
			} else {
				transactionResponse.setMessage("Transaction Failed!!! The sender's account number: "
						+ responseEntity.getBody().getAccountnumber()
						+ " has insufficient funds to perform transaction!!!");
				transactionResponse.setStatus(400);
				transactionResponse.setFaultCode("ERR-RBA-1001-SEND");
				return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
			}
		}
		return ResponseEntity.ok(transactionResponse);
	}

	/**
	 * @author sidheshwar Endpoint to transfer money within from one account to
	 *         different account belonging to SAME customer.
	 *
	 * @author sidheshwars
	 * @param transferRequestInSelfAccountDTO The transfer request details provided
	 *                                        in the request body.
	 * @return ResponseEntity<TransactionResponse> The response entity containing
	 *         the result of the money transfer.
	 * @throws JsonProcessingException
	 */
	@PostMapping("/transferWithinSelfAccounts")
	@Operation(summary = "Create a new transfer request to transfer money to one of the self accounts", description = "Endpoint to transfer amount to one of the self accounts.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Transferred amount to another account successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<TransactionResponse> transferMoneyWithinSelfAccounts(
			@RequestBody @Valid TransferRequestInSelfAccountDTO transferRequestInSelfAccountDTO)
			throws JsonProcessingException {

		String url = "http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/"
				+ transferRequestInSelfAccountDTO.getFromAccountNumber();

		log.info("Total Amount to be transfered: {}", transferRequestInSelfAccountDTO.getTransferAmount());

		TransactionResponse transactionResponse = new TransactionResponse();

		if (transferRequestInSelfAccountDTO.getTransactionCategory() != null) {
			log.info("Requested transaction category is : {}",
					transferRequestInSelfAccountDTO.getTransactionCategory());
		} else {
			log.info("Requested transaction category is : {}", TransactionCategory.miscellaneous);
		}

		ResponseEntity<Accounts> responseEntity = null;

		try {
			responseEntity = restTemplate.getForEntity(url, Accounts.class);
			log.info("Received account details in TransactionController Class: {}", responseEntity.getBody());

		} catch (HttpClientErrorException notFoundException) {
			TransactionResponse errorResponse = new TransactionResponse(null, "Account details not found", 404,
					"ERR-RBA-1006");
			log.info("Account details not found for : {}", transferRequestInSelfAccountDTO.getFromAccountNumber());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		if (responseEntity.getBody() != null && responseEntity.getBody().getAccountstatus().equals("BLOCKED")) {
			transactionResponse.setMessage("Transaction Failed!!! The sender's account number: "
					+ responseEntity.getBody().getAccountnumber() + " is blocked for all type of transactions!!!");
			transactionResponse.setStatus(400);
			transactionResponse.setFaultCode("ERR-RBA-100C&DB-SEND");
			return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
		} else {
			if (responseEntity.getBody().getAccountstatus().equals("DEBITBLOCKED")) {
				transactionResponse.setMessage("Transaction Failed!!! The sender's account number: "
						+ responseEntity.getBody().getAccountnumber() + "is blocked for all DEBIT transactions!!!");
				transactionResponse.setStatus(400);
				transactionResponse.setFaultCode("ERR-RBA-100DB-SEND");
				return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
			} else if (responseEntity.getBody().getAccountstatus().equals("ACTIVE")
					|| responseEntity.getBody().getAccountstatus().equals("CREDITBLOCKED")) {

				int senderCustomerId = responseEntity.getBody().getCustomerid();
				double senderTotalBalance = responseEntity.getBody().getTotalbalance();
				String senderAccountType = responseEntity.getBody().getAccounttype();

				if (senderTotalBalance >= transferRequestInSelfAccountDTO.getTransferAmount()) {
					TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
					transferRequestDTO.setFromAccountNumber(transferRequestInSelfAccountDTO.getFromAccountNumber());
					transferRequestDTO.setTransactionNote(transferRequestInSelfAccountDTO.getTransactionNote());
					transferRequestDTO.setTransferAmount(transferRequestInSelfAccountDTO.getTransferAmount());
					if (transferRequestInSelfAccountDTO.getTransactionCategory() != null) {
						transferRequestDTO
								.setTransactionCategory(transferRequestInSelfAccountDTO.getTransactionCategory());
					} else {
						transferRequestDTO.setTransactionCategory(TransactionCategory.miscellaneous);
					}

					List<BeneficiaryDTO> setBeneficiary = new ArrayList<BeneficiaryDTO>();

					BeneficiaryDTO beneficiaryDTO = new BeneficiaryDTO();
					beneficiaryDTO.setBeneficiaryAccountNumber(transferRequestInSelfAccountDTO.getToAccountNumber());

					setBeneficiary.add(beneficiaryDTO);

					transferRequestDTO.setBeneficiaryName(setBeneficiary);

					transactionResponse = transactionService.transferMoneyWithinAccounts(transferRequestDTO,
							senderTotalBalance, senderCustomerId, senderAccountType); // service method call

				} else {
					transactionResponse.setMessage("Transaction Failed!!! The sender's account number: "
							+ responseEntity.getBody().getAccountnumber()
							+ " has insufficient funds to perform transaction!!!");
					transactionResponse.setStatus(400);
					transactionResponse.setFaultCode("ERR-RBA-1001-SEND");
					return new ResponseEntity<>(transactionResponse, HttpStatus.BAD_REQUEST);
				}
			}
		}
		return ResponseEntity.ok(transactionResponse);
	}

//	****************************Transaction filter by expense category*********************************************************

	/**
	 * Endpoint to retrieve yearly transactions by account number, account type,
	 * expense category, and year.
	 *
	 * @author sidheshwars
	 * @param accountNumber The account number for which transactions are to be
	 *                      retrieved.
	 * @param accountType   The type of account (e.g., savings or currents).
	 * @param year          The year for which transactions are to be retrieved.
	 * @return ResponseEntity<Object> The response entity containing the yearly
	 *         transactions by expense category.
	 */

	@Operation(summary = "Get Yearly transactions by Account number ,Account type ,Expense category and Year")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })
	@GetMapping("/expense/{accountNumber}/{accountType}/yearly/{year}")
	public ResponseEntity<Object> getYearlyTransactionsByExpenseCategory(@PathVariable Long accountNumber,
			@PathVariable String accountType, @PathVariable int year) {
		log.info(
				"Received request to get yearly transactions by expense category for accountNumber: {}, accountType: {}, year: {}",
				accountNumber, accountType, year);

		try {
			ExpenseCategoryResponse yearlyTransactionsByAccountNumberAndExpenseCategory = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				yearlyTransactionsByAccountNumberAndExpenseCategory = savingsTransactionService
						.getYearlyTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType, year);
			} else if ("current".equalsIgnoreCase(accountType)) {
				yearlyTransactionsByAccountNumberAndExpenseCategory = currentsTransactionService
						.getYearlyTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType, year);
			}

			if (yearlyTransactionsByAccountNumberAndExpenseCategory == null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
			}

			return ResponseEntity.status(HttpStatus.OK).body(yearlyTransactionsByAccountNumberAndExpenseCategory);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	/**
	 * Endpoint to retrieve monthly transactions by account number, account type,
	 * expense category, and month for the ongoing year.
	 * 
	 * @author sidheshwars
	 * @param accountNumber The account number for which transactions are to be
	 *                      retrieved.
	 * @param accountType   The type of account (e.g., savings or currents).
	 * @param month         The month for which transactions are to be retrieved.
	 * @return ResponseEntity<Object> The response entity containing the monthly
	 *         transactions by expense category.
	 */
	@Operation(summary = "Get Monthly transactions by Account number ,Account type ,Expense category and Month for ongoing year")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })
	@GetMapping("/expense/{accountNumber}/{accountType}/monthly/{month}")
	public ResponseEntity<Object> getMonthlyTransactionsByExpenseCategory(@PathVariable Long accountNumber,
			@PathVariable String accountType, @PathVariable int month) {
		log.info(
				"Received request to get monthly transactions by expense category for accountNumber: {}, accountType: {}, month: {}",
				accountNumber, accountType, month);

		try {
			ExpenseCategoryResponse monthlyTransactionsByAccountNumberAndExpenseCategory = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			int year = Year.now().getValue();

			if ("savings".equalsIgnoreCase(accountType)) {
				monthlyTransactionsByAccountNumberAndExpenseCategory = savingsTransactionService
						.getMonthlyTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType, year,
								month);
			} else if ("current".equalsIgnoreCase(accountType)) {
				monthlyTransactionsByAccountNumberAndExpenseCategory = currentsTransactionService
						.getMonthlyTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType, year,
								month);
			}

			if (monthlyTransactionsByAccountNumberAndExpenseCategory == null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
			}

			return ResponseEntity.status(HttpStatus.OK).body(monthlyTransactionsByAccountNumberAndExpenseCategory);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	/**
	 * Endpoint to retrieve weekly transactions by account number, account type, and
	 * expense category for the ongoing year and month.
	 *
	 * @author sidheshwars
	 * @param accountNumber The account number for which transactions are to be
	 *                      retrieved.
	 * @param accountType   The type of account (e.g., savings or currents).
	 * @return ResponseEntity<Object> The response entity containing the weekly
	 *         transactions by expense category.
	 */
	@Operation(summary = "Get Weekly transactions by Account number ,Account type ,Expense category for ongoing year and month")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })
	@GetMapping("/expense/{accountNumber}/{accountType}/weekly")
	public ResponseEntity<Object> getWeeklyTransactionsByExpenseCategory(@PathVariable Long accountNumber,
			@PathVariable String accountType) {
		log.info(
				"Received request to get weekly transactions by expense category for accountNumber: {}, accountType: {}",
				accountNumber, accountType);

		try {
			ExpenseCategoryResponse weeklyTransactionsByAccountNumberAndExpenseCategory = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				weeklyTransactionsByAccountNumberAndExpenseCategory = savingsTransactionService
						.getWeeklyTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType);
			} else if ("current".equalsIgnoreCase(accountType)) {
				weeklyTransactionsByAccountNumberAndExpenseCategory = currentsTransactionService
						.getWeeklyTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType);
			}

			if (weeklyTransactionsByAccountNumberAndExpenseCategory == null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
			}

			return ResponseEntity.status(HttpStatus.OK).body(weeklyTransactionsByAccountNumberAndExpenseCategory);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	/**
	 * Endpoint to retrieve yesterday's or today's transactions by account number,
	 * account type, and expense category.
	 *
	 * @author sidheshwars
	 * @param accountNumber The account number for which transactions are to be
	 *                      retrieved.
	 * @param accountType   The type of account (e.g., savings or currents).
	 * @param date          The transaction date (yesterday or today).
	 * @return ResponseEntity<Object> The response entity containing yesterday's or
	 *         today's transactions by expense category.
	 */
	@Operation(summary = "Get Yesterdays or Todays transactions by Account number ,Account type ,Expense category")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })
	@GetMapping("/expense/{accountNumber}/{accountType}/yesterdayOrToday/{date}")
	public ResponseEntity<Object> getYesterdaysTransactionsByExpenseCategory(@PathVariable Long accountNumber,
			@PathVariable String accountType, @PathVariable DayTransaction date) {
		log.info("Received request to get {} transactions by expense category for accountNumber: {}, accountType: {}",
				date, accountNumber, accountType);

		try {
			ExpenseCategoryResponse yesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				yesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory = savingsTransactionService
						.getYesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType,
								date);
			} else if ("current".equalsIgnoreCase(accountType)) {
				yesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory = currentsTransactionService
						.getYesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory(accountNumber, accountType,
								date);
			}

			if (yesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory == null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
			}

			return ResponseEntity.status(HttpStatus.OK)
					.body(yesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	/*
	 * API to find active account of the user to avail or block the self transfer
	 * Functionality
	 */
	@GetMapping("/findactiveaccount/{customerId}")
	@Operation(summary = "Finding all active accounts", description = "Checking all active account for the Self transfer")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "more then 1 account found"),
			@ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
			@ApiResponse(responseCode = "204", description = "Only one account found"),
			@ApiResponse(responseCode = "404", description = "No account found") })
	public ResponseEntity<?> findAccounts(@PathVariable int customerId) {
		log.info("Inside the API findAccounts with the customerId " + customerId);
		String url = "http://localhost:1012/api/retailbanking/accounts/customer/" + customerId;

		ResponseEntity<FindAccountResponce> responseEntity = restTemplate.getForEntity(url, FindAccountResponce.class);

		@SuppressWarnings("unused")
		FindAccountResponce findAccountResponce = null;
		List<Accounts> allAccounts = responseEntity.getBody().getData();

		List<Accounts> activeAccounts = transactionService.findAllAccount(allAccounts);

		if (activeAccounts.size() > 1) {
			log.info("Inside the if part of findAccount API, Returing responce code 200 with the list of account");
			return ResponseEntity.ok(activeAccounts);
		} else {
			log.info("Inside the else part of findAccount API, Returing responce code 204 as only 1 account found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(activeAccounts);
		}

	}

	/* **************************************************** */

	/*
	 * Get All The transaction details Based on LatestTransaction, Monthly
	 * Transaction, Quarterly Transaction And Yearly Transaction.
	 * 
	 * @param accountNumber The Account Number of the customer.
	 * 
	 * @return ResponseEntity<Object> The response entity containing the result of
	 * the transaction.
	 */

	@Operation(summary = "Get latest transactions by Account number and Account type")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })

	@GetMapping("/getLatestTransactions/{accountNumber}/{accountType}")
	public ResponseEntity<Object> getLatestTransactions(@PathVariable Long accountNumber,
			@PathVariable String accountType) {
		log.info("Received request to get latest transactions for accountNumber: {} and accountType: {}", accountNumber,
				accountType);

		try {
			List<TransactionDTOResponse> latestTransactionsByAccountNumber = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				log.info("Invalid account type Throwing exception");
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				latestTransactionsByAccountNumber = savingsTransactionService
						.getLatestTransactionsByAccountNumber(accountNumber, accountType);
				log.info("Fetching savings account transactions");
			} else if ("current".equalsIgnoreCase(accountType)) {
				log.info("Fetching Current account transactions");
				latestTransactionsByAccountNumber = currentsTransactionService
						.getLatestTransactionsByAccountNumber(accountNumber, accountType);
			}

			if (latestTransactionsByAccountNumber == null || latestTransactionsByAccountNumber.isEmpty()) {
				log.info("There are no transactions for the account");
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.NO_CONTENT.value(), "No Data Found", null));
			}
			return ResponseEntity.status(HttpStatus.OK).body(new AccountStatementResponse(HttpStatus.OK.value(),
					"Successfully retrieved transactions", latestTransactionsByAccountNumber));

		} catch (RuntimeException e) {
			// Catch RuntimeException for invalid account type or other runtime exceptions
			log.error("Throwing Runtime Exception");
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			// Catch any other exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	@Operation(summary = "Get Monthly transactions by Account number ,Account type and Month")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })

	@GetMapping("/{accountNumber}/{accountType}/monthly/{year}/{month}")
	public ResponseEntity<Object> getMonthlyTransactions(@PathVariable Long accountNumber,
			@PathVariable String accountType, @PathVariable int year, @PathVariable int month) {
		log.info(
				"Received request to get monthly transactions for accountNumber: {}, accountType: {}, year: {}, month: {}",
				accountNumber, accountType, year, month);

		try {
			List<TransactionDTOResponse> monthlyTransactionsByAccountNumber = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				monthlyTransactionsByAccountNumber = savingsTransactionService
						.getMonthlyTransactionsByAccountNumber(accountNumber, accountType, year, month);
			} else if ("current".equalsIgnoreCase(accountType)) {
				monthlyTransactionsByAccountNumber = currentsTransactionService
						.getMonthlyTransactionsByAccountNumber(accountNumber, accountType, year, month);
			}

			if (monthlyTransactionsByAccountNumber == null || monthlyTransactionsByAccountNumber.isEmpty()) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.NO_CONTENT.value(), "No Data Found", null));
			}

			return ResponseEntity.status(HttpStatus.OK).body(new AccountStatementResponse(HttpStatus.OK.value(),
					"Successfully retrieved transactions", monthlyTransactionsByAccountNumber));

		} catch (RuntimeException e) {
			// Catch RuntimeException for invalid account type or other runtime exceptions
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			// Catch any other exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	@Operation(summary = "Get Quaterly transactions by Account number ,Account type,Year and Quarter")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })

	@GetMapping("/{accountNumber}/{accountType}/quarterly/{year}/{quarter}")
	public ResponseEntity<Object> getQuarterlyTransactions(@PathVariable Long accountNumber,
			@PathVariable String accountType, @PathVariable int year, @PathVariable int quarter) {
		log.info(
				"Received request to get quarterly transactions for accountNumber: {}, accountType: {}, year: {}, quarter: {}",
				accountNumber, accountType, year, quarter);

		try {
			List<TransactionDTOResponse> quarterlyTransactionsByAccountNumber = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				quarterlyTransactionsByAccountNumber = savingsTransactionService
						.getQuarterlyTransactionsByAccountNumber(accountNumber, accountType, year, quarter);
			} else if ("current".equalsIgnoreCase(accountType)) {
				quarterlyTransactionsByAccountNumber = currentsTransactionService
						.getQuarterlyTransactionsByAccountNumber(accountNumber, accountType, year, quarter);
			}

			if (quarterlyTransactionsByAccountNumber == null || quarterlyTransactionsByAccountNumber.isEmpty()) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.NO_CONTENT.value(), "No Data Found", null));
			}

			return ResponseEntity.status(HttpStatus.OK).body(new AccountStatementResponse(HttpStatus.OK.value(),
					"Successfully retrieved transactions", quarterlyTransactionsByAccountNumber));

		} catch (RuntimeException e) {
			// Catch RuntimeException for invalid account type or other runtime exceptions
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			// Catch any other exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	@Operation(summary = "Get Yearly transactions by Account number ,Account type and Year")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })

	@GetMapping("/{accountNumber}/{accountType}/yearly/{year}")
	public ResponseEntity<Object> getYearlyTransactions(@PathVariable Long accountNumber,
			@PathVariable String accountType, @PathVariable int year) {
		log.info("Received request to get yearly transactions for accountNumber: {}, accountType: {}, year: {}",
				accountNumber, accountType, year);

		try {
			List<TransactionDTOResponse> yearlyTransactionsByAccountNumber = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				yearlyTransactionsByAccountNumber = savingsTransactionService
						.getYearlyTransactionsByAccountNumber(accountNumber, accountType, year);
			} else if ("current".equalsIgnoreCase(accountType)) {
				yearlyTransactionsByAccountNumber = currentsTransactionService
						.getYearlyTransactionsByAccountNumber(accountNumber, accountType, year);
			}

			if (yearlyTransactionsByAccountNumber == null || yearlyTransactionsByAccountNumber.isEmpty()) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ErrorResponse(HttpStatus.NO_CONTENT.value(), "No Data Found", null));
			}

			return ResponseEntity.status(HttpStatus.OK).body(new AccountStatementResponse(HttpStatus.OK.value(),
					"Successfully retrieved transactions", yearlyTransactionsByAccountNumber));

		} catch (RuntimeException e) {
			// Catch RuntimeException for invalid account type or other runtime exceptions
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", null));
		} catch (Exception e) {
			// Catch any other exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	/**
	 * @author nitinab Retrieves Benificiary for a given customer ID.
	 *
	 * @param customerid The ID of the customer.
	 * @return ResponseEntity with GetBenificiaryResponse containing customerId,
	 *         status, and message.
	 */

	@Operation(summary = "Get Benificiary by customerId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved Benificiary Details"),
			@ApiResponse(responseCode = "404", description = "No Benificiary Details found for the given customerId") })

	@GetMapping("/benificiary/{customerId}")
	public ResponseEntity<GetBenificiaryResponse> getBenificiaryByCustomerId(@PathVariable int customerId) {

		log.info("Received request to Get Benificiary by customerId: {}", customerId);

		List<BenificiaryAccount> benificiaryAccounts = transactionService.getBenificiaryByCustomerId(customerId);

		if (benificiaryAccounts != null && !benificiaryAccounts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(new GetBenificiaryResponse(HttpStatus.OK.value(),
					"Successfully retrieved Benificiary Details", benificiaryAccounts));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GetBenificiaryResponse(HttpStatus.NOT_FOUND.value(),
							"No Benificiary Details found for the given customerId", benificiaryAccounts));
		}
	}

	/**
	 * Retrieves the budget of a requested transaction category for a particular
	 * account number. This endpoint fetches the budget setup response based on the
	 * provided account number.
	 * 
	 * @author sidheshwars
	 * @param accountNumber The account number for which the budget is requested.
	 * @return ResponseEntity with the budget setup response if successful,
	 *         otherwise appropriate error responses.
	 **/
	@Operation(summary = "Get budget limit by account number")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Budget limit retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping("/fetchBudgets/accountNumber/{accountNumber}")
	public ResponseEntity<BudgetSetupResponse> getBudgetLimitByAccountNumber(@PathVariable long accountNumber) {
		try {
			log.info("Fetching budget for requested account number: {}", accountNumber);

			if (accountNumber <= 0) {
				return ResponseEntity.badRequest()
						.body(new BudgetSetupResponse(400, "Account number must be greater than zero.", null));
			}

			BudgetSetupResponse budgetSetupResponse = transactionService.getBudgetLimitByExpenseCategory(accountNumber);

			if (budgetSetupResponse != null) {
				return ResponseEntity.ok(budgetSetupResponse);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			log.error("Error occurred while fetching budget for account number {}: {}", accountNumber, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Setting up the budget for the requested transaction categories for a
	 * particular account for ongoing month.
	 * 
	 * @author sidheshwars
	 * @param accountNumber The account number for which the budget is requested.
	 * @return ResponseEntity with the budget setup response if successful,
	 *         otherwise appropriate error responses.
	 **/
	@Operation(summary = "Setting up the budget for the requested transaction categories for a particular account for ongoing month.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Budget limit updated successfully"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping("/setupBudget/accountNumber/{accountNumber}")
	public ResponseEntity<BudgetSetupResponse> updateBudgetLimitByAccountNumber(@PathVariable long accountNumber,
			@RequestBody @Valid BudgetSetupDTO budgetSetupDTO) {
		try {

			if (accountNumber <= 0) {
				return ResponseEntity.badRequest()
						.body(new BudgetSetupResponse(400, "Account number must be greater than zero.", null));
			}

			if (budgetSetupDTO != null) {

				BudgetSetupResponse budgetSetupResponse = transactionService
						.updateBudgetLimitByAccountNumber(accountNumber, budgetSetupDTO);
				if (budgetSetupResponse != null) {
					return ResponseEntity.ok(budgetSetupResponse);
				} else {
					return ResponseEntity.notFound().build();
				}
			} else {
				return ResponseEntity.badRequest().body(new BudgetSetupResponse(400, "Invalid input", null));
			}

		} catch (Exception e) {
			log.error("Error occurred while fetching budget for account number {}: {}", accountNumber, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Endpoint to retrieve transactions between 2 dates by account number and
	 * account type.
	 *
	 * @author vruttantp
	 * @param accountNumber The account number for which transactions are to be
	 *                      retrieved.
	 * @param accountType   The type of account (e.g., savings or current).
	 * @param startDate     The start date for transaction period.
	 * @param endDate       The end date for transaction period
	 * @return ResponseEntity<Object> The response entity containing transactions
	 *         between the start and end dates.
	 */
	@Operation(summary = "Get transactions between 2 dates by Account number ,Account type")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Account not found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "204", description = "No Data found") })
	@GetMapping("/between/{accountNumber}/{accountType}/{startDate}/{endDate}")
	public ResponseEntity<Object> getTransactionsBetweenTwoDates(@PathVariable Long accountNumber,
			@PathVariable String accountType, @PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
		log.info("Received request to get transactions for accountNumber: {}, accountType: {} between {} and {}",
				accountNumber, accountType, startDate, endDate);

		try {
			List<TransactionDTOResponse> rangedTransactionsByAccountNumber = null;

			if (!("savings".equalsIgnoreCase(accountType) || "current".equalsIgnoreCase(accountType))) {
				throw new RuntimeException("Invalid account type");
			}

			if ("savings".equalsIgnoreCase(accountType)) {
				rangedTransactionsByAccountNumber = savingsTransactionService
						.getTransactionsBetweenTwoDatesByAccountNumber(accountNumber, accountType,
								startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

			} else if ("current".equalsIgnoreCase(accountType)) {
				rangedTransactionsByAccountNumber = currentsTransactionService
						.getTransactionsBetweenTwoDatesByAccountNumber(accountNumber, accountType,
								startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
			}

			if (rangedTransactionsByAccountNumber == null || rangedTransactionsByAccountNumber.isEmpty()) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new AccountStatementErrorResponse(HttpStatus.NO_CONTENT.value(), "No Data Found"));
			}

			return ResponseEntity.status(HttpStatus.OK).body(new AccountStatementResponse(HttpStatus.OK.value(),
					"Successfully retrieved transactions", rangedTransactionsByAccountNumber));

		} catch (RuntimeException e) {
			// Catch RuntimeException for invalid account type or other runtime exceptions
			log.error(e.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AccountStatementErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request"));
		} catch (Exception e) {
			// Catch any other exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AccountStatementResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
		}
	}

	/**
	 * @author nirajku
	 * 
	 *         Adding benificiary for the payment by taking their data
	 * 
	 * @param customerId    Id of the customer who is adding
	 * @param accountnumber benificiary account number
	 * @param name          benificiary account number
	 * @param email         Email of benificiary
	 * 
	 */
	@PostMapping("/benificiary/add/{customerId}")
	@Operation(summary = "Adding Benificiary", description = "Add the benificiary with the account number and given details")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Benificiary added successfully."),
			@ApiResponse(responseCode = "400", description = "Account number of benificiary not found. "),
			@ApiResponse(responseCode = "100", description = "Account number of benificiary is already added ") })
	public ResponseEntity<BenificiaryResponse> addBeneficiary(@PathVariable int customerId,
			@RequestParam long accountNumber, @RequestParam String name, @RequestParam String email) {
		log.info("Inside the controller of adding benificiary with account number " + accountNumber);
		AddBenificiaryDTO addBenificiaryDTO = new AddBenificiaryDTO();
		addBenificiaryDTO.setCustomerId(customerId);
		addBenificiaryDTO.setAccountNumber(accountNumber);
		addBenificiaryDTO.setName(name);
		addBenificiaryDTO.setEmail(email);
		BenificiaryResponse benificiaryResponse = transactionService.addBeneficiary(addBenificiaryDTO);
		log.info("End of controller of adding benificiary ");
 
		return ResponseEntity.status(benificiaryResponse.getStatus()).body(benificiaryResponse);
	}
	
	/**
	 * @author vruttantp
	 *
	 * Get the split of the monthly expenses for a particular category.
	 * @param accountNumber 	The account number for which expenses are to be
	 *                      	retrieved.
	 * @param accountType   	The type of account (e.g., savings or current).
	 * @param expenseCategory 	The name of the category for which expenses are to be retrieved.
	 *
	 * @return ResponseEntity<Object> The response entity containing dates and the expense
	 * 								  on those dates for the chosen category, or an error
	 * 								  response if the request fails.      
	 * */
	@Operation(summary = "Get monthly expenses by account number, account type and expense category")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Successfully retrieved monthly expenses"),
	        @ApiResponse(responseCode = "400", description = "Bad Request"),
	        @ApiResponse(responseCode = "404", description = "Account not found"),
	        @ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@GetMapping("/expenses/{accountNumber}/{accountType}/{expenseCategory}")
	public ResponseEntity<Object> getMonthlyExpensesByCategoryAndAccountNumberAndAccountType(
	        @PathVariable Long accountNumber,
	        @PathVariable String accountType,
	        @PathVariable String expenseCategory) {
 
	    try {
	        // Call the service method
	    	ExpenseCategoryResponse expenseCategoryResponse= transactionService.
	    			getMonthlyExpensesByCategoryAndAccountNumberAndAccountType( accountNumber,
	    					accountType, TransactionCategory.valueOf(expenseCategory.toLowerCase()));
 
	        // Construct the success response
			return ResponseEntity.status(HttpStatus.OK).body(expenseCategoryResponse);
 
	    } catch (RuntimeException e) {
	        // Catch RuntimeException for invalid account type or other runtime exceptions
	        log.error(e.toString());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",null));
 
	    } catch (Exception e) {
	        // Catch any other exceptions
	    	log.error(e.toString());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new AccountStatementResponse(
	    					HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null));
	    }
	}
	
	@PostMapping("/addBudget/accountNumber/{accountNumber}")
	public ResponseEntity<BudgetSetupResponse> addBudgetLimitByAccountNumber(@PathVariable long accountNumber) {
	    try {

            if (accountNumber <= 0) {
                return ResponseEntity.badRequest().body(new BudgetSetupResponse(400,"Account number must be greater than zero.",null));
               }
           
	    		
		        BudgetSetupResponse budgetSetupResponse = transactionService.addBudgetLimitByAccountNumber(accountNumber);
	            if(budgetSetupResponse.getStatus()==200) {
			        return ResponseEntity.ok(budgetSetupResponse);
	            }else {
		        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	            }
		        
		    
	        } catch (Exception e) {
	        	log.error("Error occurred while fetching budget for account number {}: {}", accountNumber, e.getMessage());
	        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	    
	}
}