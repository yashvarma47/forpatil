package com.thinknxt.rba.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thinknxt.rba.dto.Accounts;
import com.thinknxt.rba.dto.AddBenificiaryDTO;
import com.thinknxt.rba.dto.BudgetSetupDTO;
import com.thinknxt.rba.dto.CustomerDTO;
import com.thinknxt.rba.dto.EmailRequest;
import com.thinknxt.rba.dto.TransactionRequestDTO;
import com.thinknxt.rba.dto.TransferRequestDTO;
import com.thinknxt.rba.entities.BenificiaryAccount;
import com.thinknxt.rba.entities.Budget;
import com.thinknxt.rba.entities.CurrentsTransactionEntity;
import com.thinknxt.rba.entities.SavingsTransactionEntity;
import com.thinknxt.rba.entities.TransactionEntity;
import com.thinknxt.rba.exception.AccountDetailsNotFoundException;
import com.thinknxt.rba.mapper.DtoEntityMapper;
import com.thinknxt.rba.repository.BenificiaryAccountRepository;
import com.thinknxt.rba.repository.BudgetRepository;
import com.thinknxt.rba.repository.CurrentsTransactionRepository;
import com.thinknxt.rba.repository.SavingsTransactionRepository;
import com.thinknxt.rba.repository.TransactionRepository;
import com.thinknxt.rba.response.BenificiaryResponse;
import com.thinknxt.rba.response.BudgetSetupResponse;
import com.thinknxt.rba.response.ExpenseCategoryResponse;
import com.thinknxt.rba.response.TransactionResponse;
import com.thinknxt.rba.utils.TransactionCategory;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private final BenificiaryAccountRepository benificiaryAccountRepository;

	private final RestTemplate restTemplate;

	private final DtoEntityMapper dtoEntityMapper;

	private TransactionRepository transactionRepository;

	private SavingsTransactionRepository savingTransactionRepository;

	private CurrentsTransactionRepository currentsTransactionRepository;

	private BudgetRepository budgetRepository;

//	@Autowired
	public TransactionServiceImpl(DtoEntityMapper dtoEntityMapper, RestTemplate restTemplate,
			TransactionRepository transactionRepository, SavingsTransactionRepository savingTransactionRepository,
			CurrentsTransactionRepository currentsTransactionRepository, BudgetRepository budgetRepository,
			BenificiaryAccountRepository benificiaryAccountRepository) {
		this.dtoEntityMapper = dtoEntityMapper;
		this.restTemplate = restTemplate;
		this.transactionRepository = transactionRepository;
		this.currentsTransactionRepository = currentsTransactionRepository;
		this.savingTransactionRepository = savingTransactionRepository;
		this.budgetRepository = budgetRepository;
		this.benificiaryAccountRepository = benificiaryAccountRepository;
	}

	/**
	 * Creates a new transaction based on the provided details.
	 * 
	 * @param transactionRequestDTO The transaction details provided in the request.
	 * @param account               The account details associated with the
	 *                              transaction.
	 * @return TransactionResponse The response containing the result of the
	 *         transaction.
	 */
	@Transactional
	public TransactionResponse createTransaction(TransactionRequestDTO transactionRequestDTO, Accounts account) {
		if (transactionRequestDTO != null) {
 
			String transactionId = UniqueStringGenerator.generateUniqueRandomNumber();
 
			String accountType = account.getAccounttype(); // saving or credits
 
			String accountStatus = account.getAccountstatus(); // active,credit blocked
 
			Double totalBalance = account.getTotalbalance();
 
			TransactionEntity transactionEntity = new TransactionEntity();
 
			String url = "http://localhost:1012/api/retailbanking/accounts/updateAmount?newAmount=" + totalBalance
					+ "&" + "accountNumber=" + transactionRequestDTO.getAccountNumber();
 
			MultiValueMap<String, Object> requestParams = new LinkedMultiValueMap<>();
 
			if (StringUtils.equalsIgnoreCase(accountType, "SAVINGS")) {
 
				SavingsTransactionEntity savingsTransactionEntity = new SavingsTransactionEntity();
 
				savingsTransactionEntity.setAccountNumber(transactionRequestDTO.getAccountNumber());
				savingsTransactionEntity.setTransactionAmount(transactionRequestDTO.getTransactionAmount());
				savingsTransactionEntity.setTransactionTime(LocalDateTime.now());
				savingsTransactionEntity.setNarratives(transactionRequestDTO.getNarratives());
				savingsTransactionEntity.setTransactionId(transactionId);
				savingsTransactionEntity.setCustomerId(transactionRequestDTO.getCustomerId());
 
				savingsTransactionEntity.setRecipient(transactionRequestDTO.getAccountNumber()); // teller sending cash to entered account number
 
				String transactionType = transactionRequestDTO.getTransactionType(); // debit or credit
 
				if (StringUtils.equalsIgnoreCase(transactionType, "CREDIT")) {
					totalBalance = totalBalance + transactionRequestDTO.getTransactionAmount();
					savingsTransactionEntity.setRemainingBalance(totalBalance);
					savingsTransactionEntity.setTransactionType("Credit");
 
					transactionEntity = transactionEntityTransformer(transactionId,"SUCCESS","CASH");
 
					savingsTransactionEntity.setTransaction(transactionEntity);
 
					requestParams.add("newAmount", totalBalance);
					requestParams.add("accountNumber", transactionRequestDTO.getAccountNumber());
 
					restTemplate.postForEntity(url, requestParams, Void.class);
					log.info("Credit Transaction: Updated total balance: {}", totalBalance);
 
				} else if (StringUtils.equalsIgnoreCase(transactionType, "DEBIT")) {
 
					if (transactionRequestDTO.getTransactionAmount() < totalBalance) {
						totalBalance = totalBalance - transactionRequestDTO.getTransactionAmount();
						savingsTransactionEntity.setRemainingBalance(totalBalance);
						savingsTransactionEntity.setTransactionType("Debit");
						
						transactionEntity = transactionEntityTransformer(transactionId,"SUCCESS","CASH");
						
						savingsTransactionEntity.setTransaction(transactionEntity);
 
						requestParams.add("newAmount", totalBalance);
						requestParams.add("accountNumber", transactionRequestDTO.getAccountNumber());
 
						restTemplate.postForEntity(url, requestParams, Void.class);
 
						log.info("Debit Transaction: Updated total balance: {}", totalBalance);
 
					} else {
						savingsTransactionEntity.setTransactionType("Debit");
						savingsTransactionEntity.setRemainingBalance(totalBalance);
 
						transactionEntity = transactionEntityTransformer(transactionId,"FAILED","CASH");
 
						savingsTransactionEntity.setTransaction(transactionEntity);
						savingTransactionRepository.save(savingsTransactionEntity);
 
						return new TransactionResponse(null, "Insufficient funds!!!", 404, "ERR-RBA-1001");
					}
				}
				savingTransactionRepository.save(savingsTransactionEntity);
				
				return new TransactionResponse(null, "Transaction has been successful!!!", 200, null);
			} // saving ends
 
			else if (StringUtils.equalsIgnoreCase(accountType, "CURRENT")) {
 
				CurrentsTransactionEntity currentsTransactionEntity = new CurrentsTransactionEntity();
 
				currentsTransactionEntity.setAccountNumber(transactionRequestDTO.getAccountNumber());
				currentsTransactionEntity.setTransactionAmount(transactionRequestDTO.getTransactionAmount());
				currentsTransactionEntity.setTransactionTime(LocalDateTime.now());
				currentsTransactionEntity.setNarratives(transactionRequestDTO.getNarratives());
				currentsTransactionEntity.setTransactionId(transactionId);
				currentsTransactionEntity.setCustomerId(transactionRequestDTO.getCustomerId());
 
				currentsTransactionEntity.setRecipient(transactionRequestDTO.getAccountNumber()); // teller sending cash
																									// to entered
																									// acocunt number
 
				String transactionType = transactionRequestDTO.getTransactionType(); // debit or credit
 
				if (StringUtils.equalsIgnoreCase(transactionType, "CREDIT")) {
					totalBalance = totalBalance + transactionRequestDTO.getTransactionAmount();
					currentsTransactionEntity.setRemainingBalance(totalBalance);
					currentsTransactionEntity.setTransactionType("Credit");
					
					transactionEntity = transactionEntityTransformer(transactionId,"SUCCESS","CASH");
 
					currentsTransactionEntity.setTransaction(transactionEntity);
 
					requestParams.add("newAmount", totalBalance);
					requestParams.add("accountNumber", transactionRequestDTO.getAccountNumber());
 
					restTemplate.postForEntity(url, requestParams, Void.class);
					log.info("Credit Transaction: Updated total balance: {}", totalBalance);
 
				} else if (StringUtils.equalsIgnoreCase(transactionType, "DEBIT")) {
					if (transactionRequestDTO.getTransactionAmount() < totalBalance) {
						totalBalance = totalBalance - transactionRequestDTO.getTransactionAmount();
						currentsTransactionEntity.setRemainingBalance(totalBalance);
						currentsTransactionEntity.setTransactionType("Debit");
						
						transactionEntity = transactionEntityTransformer(transactionId,"SUCCESS","CASH");
 
						currentsTransactionEntity.setTransaction(transactionEntity);
 
						requestParams.add("newAmount", totalBalance);
						requestParams.add("accountNumber", transactionRequestDTO.getAccountNumber());
 
						restTemplate.postForEntity(url, requestParams, Void.class);
						log.info("Debit Transaction: Updated total balance: {}", totalBalance);
 
					} else {
						log.info("Insufficient funds!!!");
						currentsTransactionEntity.setTransactionType("Debit");
						currentsTransactionEntity.setRemainingBalance(totalBalance);
 
						transactionEntity = transactionEntityTransformer(transactionId,"FAILED","CASH");
 
						currentsTransactionEntity.setTransaction(transactionEntity);
 
						currentsTransactionRepository.save(currentsTransactionEntity);
 
						return new TransactionResponse(null, "Insufficient funds!!!", 404, "ERR-RBA-1001");
					}
				} else {
					return new TransactionResponse(null, "Invalid Transaction Type : Should be Credit or Debit!!!", 400,
							"ERR-RBA-1003");
				}
				currentsTransactionRepository.save(currentsTransactionEntity);
				return new TransactionResponse(null, "Transaction has been successful!!!", 200, null);
			} // current ends
		}
		return new TransactionResponse(null, "Transaction has been Failed!!!", 404, "ERR-RBA-1002");
	}
	
public TransactionEntity transactionEntityTransformer(String transactionId,String transactionStatus,String transactionMode){
		
		TransactionEntity transactionEntity = new TransactionEntity();
		
		transactionEntity.setTransactionId(transactionId);
		transactionEntity.setTransactionStatus(transactionStatus);
		transactionEntity.setTransactionTime(LocalDateTime.now());
		transactionEntity.setTransactionMode(transactionMode);
		return transactionRepository.save(transactionEntity);
		
	}

@Override
public TransactionResponse transferMoneyWithinAccounts(TransferRequestDTO transferRequestDTO,
		double senderTotalBalance, int senderCustomerId, String senderAccountType) throws JsonProcessingException {

	
	double budgetLimitForRequestedCategory = 0 ;
	
	Budget budget = budgetRepository.findByAccountNumber(transferRequestDTO.getFromAccountNumber());
	JsonNode categoryBudgetLimitAmount= null;
	JsonNode monthData = null;
	JsonNode jsonNode = null;
	String budgetResponse = "";
	String mailResponse = "";
	try {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonNode = objectMapper.readTree(budget.getBudgetData());
        int currentMonth = LocalDate.now().getMonth().getValue();
        monthData = jsonNode.get(String.valueOf(currentMonth));
        if(transferRequestDTO.getTransactionCategory()!=null) {
			transferRequestDTO.setTransactionCategory(transferRequestDTO.getTransactionCategory());
		}else {
			transferRequestDTO.setTransactionCategory(TransactionCategory.miscellaneous);
		}
        categoryBudgetLimitAmount = monthData.get(transferRequestDTO.getTransactionCategory().toString().toLowerCase()+"_threshold");
    } catch (Exception e) {
        e.printStackTrace();
    }
	
	if(budget!=null) {
		budgetLimitForRequestedCategory = categoryBudgetLimitAmount.asDouble();
		log.info("Budget for requested transaction category is : {}",budgetLimitForRequestedCategory);
		
		if(budgetLimitForRequestedCategory>0 && transferRequestDTO.getTransferAmount()>budgetLimitForRequestedCategory) {
			budgetResponse= String.format(", Budget Alert : With this transaction you're exceeding your Expense Budget for : "+transferRequestDTO.getTransactionCategory()+" for ongoing month: "+LocalDate.now().getMonth());
		}
		else if((budgetLimitForRequestedCategory>0 && transferRequestDTO.getTransferAmount()<=budgetLimitForRequestedCategory)||(budgetLimitForRequestedCategory==0.0)) {
			budgetResponse="";
		}
	}
	
	String transactionId = UniqueStringGenerator.generateUniqueRandomNumber();

	
	if (transferRequestDTO != null) {
		if (!transferRequestDTO.getBeneficiaryName().isEmpty()) {
			List<TransactionResponse> responses = transferRequestDTO.getBeneficiaryName().stream().map(t -> {
				String url = "http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/"+ t.getBeneficiaryAccountNumber();
				log.info("Fetching account details in TransactionServiceImpl Class for accountNumber: {}",t.getBeneficiaryAccountNumber());
				TransactionResponse transactionResponse = null;
				try {
					ResponseEntity<Accounts> responseEntity = restTemplate.getForEntity(url,Accounts.class);
					log.info("Received account details in TransactionServiceImpl Class: {}",responseEntity.getBody());
					transactionResponse = new TransactionResponse(responseEntity.getBody(),"Account details found", 200, null);
				} catch (HttpClientErrorException notFoundException) {
					log.error("Account details not found for accountNumber: {}", t.getBeneficiaryAccountNumber());
					transactionResponse = new TransactionResponse(null,"Account details not found for :: " + t.getBeneficiaryAccountNumber(), 404,"ERR-RBA-1006");
					return transactionResponse;
				} catch (Exception e) {
					log.error("An error occurred while fetching account details", e);
					transactionResponse = new TransactionResponse(null, "An error occurred", 500, "ERR-RBA-500");
					return transactionResponse;
				}
				return transactionResponse;
			}).collect(Collectors.toList());

			List<TransactionResponse> list = responses.stream()
					.filter(t -> t.getFaultCode() != null && t.getFaultCode().equals("ERR-RBA-1006"))
					.collect(Collectors.toList());

			if (!list.isEmpty()) {
				List<String> invalidAccountList = new ArrayList<String>();
				for (TransactionResponse transactionResponse : list) {
					invalidAccountList.add(transactionResponse.getMessage().substring(33));
				}
				throw new AccountDetailsNotFoundException(invalidAccountList,"Account details not found for accountNumber: ");
			}

			if (responses.stream().anyMatch(t -> t.getData()!=null && t.getData().getAccountstatus().equals("BLOCKED"))) {
				TransactionResponse transactionResponse = new TransactionResponse(null,"Transfer request failed as one of the beneficiaries account is blocked for all type of transactions (CREDIT/DEBIT)!!!", 404,"ERR-RBA-100C&DB-BENF");
				transactionEntityTransformer(transactionId,"FAILED","NET BANKING");
				return transactionResponse;					
			}

				for (TransactionResponse transactionResponse : responses) { // beneficiary loop

					String benefAccountType = transactionResponse.getData().getAccounttype();

					
					if (transactionResponse.getData().getAccountstatus().equals("BLOCKED")) {
						transactionEntityTransformer(transactionId,"FAILED","NET BANKING");
						return new TransactionResponse(null,"Transaction Failed!!! The destination account number: "+ transactionResponse.getData().getAccountnumber()+ " is blocked for all type of transactions (CREDIT/DEBIT)!!!",404, "ERR-RBA-100C&DB-DEST");
					}else if ((transactionResponse.getData().getAccountstatus().equals("ACTIVE")
							|| transactionResponse.getData().getAccountstatus().equals("DEBITBLOCKED"))) {

						
						Map<String, Object> map = dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
								transactionResponse, transferRequestDTO, senderTotalBalance, senderCustomerId,
								transactionId, senderAccountType, benefAccountType);

						senderTotalBalance = senderTotalBalance - transferRequestDTO.getTransferAmount();

						try {
						transactionRepository.save((TransactionEntity) map.get("transactionEntity"));
						}
						catch(Exception e) {
                            log.error("An error occurred while saving transaction", e);
                            throw e;
                        }
						
						SavingsTransactionEntity savingsTransactionEntityForSenderAccount;
						SavingsTransactionEntity savingsTransactionEntityForBeneficiaryAccount;
						
						if (map.get("transferRequestType") != null&& map.get("transferRequestType").equals("savToSav")) {

							log.info("Transferring amount from savings account: {} to another savings account: {}",transferRequestDTO.getFromAccountNumber(),transactionResponse.getData().getAccountnumber());
							try {
								 savingsTransactionEntityForSenderAccount = savingTransactionRepository.save(((SavingsTransactionEntity) map.get("savingsTransactionEntityForSenderAccount")));
								 savingsTransactionEntityForBeneficiaryAccount = savingTransactionRepository.save(((SavingsTransactionEntity) map.get("savingsTransactionEntityForBeneficiaryAccount")));
							}
							catch(Exception e) {
                                log.error("An error occurred while saving savings transaction", e);
                                throw e;
                            }
							
							
//							send mail condition
							if(savingsTransactionEntityForSenderAccount!=null && savingsTransactionEntityForBeneficiaryAccount!=null) {
								mailResponse = ", Email Alert : "+sendEmailOnSuccessfulTransaction(senderCustomerId,transferRequestDTO,transactionResponse,senderTotalBalance);
							}
	
							updateBudgetLimitByTransferAmount(budget,jsonNode,transferRequestDTO.getTransferAmount(),transferRequestDTO);
						
							
						} else if (map.get("transferRequestType") != null&& map.get("transferRequestType").equals("savToCur")) {

							log.info("Transferring amount from savings account: {} to currents account: {}",transferRequestDTO.getFromAccountNumber(),transactionResponse.getData().getAccountnumber());
							 savingsTransactionEntityForSenderAccount = savingTransactionRepository.save(((SavingsTransactionEntity) map.get("savingsTransactionEntityForSenderAccount")));
							CurrentsTransactionEntity currentsTransactionEntityForBeneficiaryAccount =currentsTransactionRepository.save(((CurrentsTransactionEntity) map.get("currentsTransactionEntityForBeneficiaryAccount")));

							//send mail condition
							if(savingsTransactionEntityForSenderAccount!=null && currentsTransactionEntityForBeneficiaryAccount!=null) {
								mailResponse = ", Email Alert : "+sendEmailOnSuccessfulTransaction(senderCustomerId,transferRequestDTO,transactionResponse,senderTotalBalance);
							}
							
							updateBudgetLimitByTransferAmount(budget,jsonNode,transferRequestDTO.getTransferAmount(),transferRequestDTO);
							
						} else if (map.get("transferRequestType") != null&& map.get("transferRequestType").equals("curToSav")) {

							log.info("Transferring amount from currents account: {} to savings account: {}",transferRequestDTO.getFromAccountNumber(),transactionResponse.getData().getAccountnumber());
							CurrentsTransactionEntity currentsTransactionEntityForSenderAccount = currentsTransactionRepository.save(((CurrentsTransactionEntity) map.get("currentsTransactionEntityForSenderAccount")));
							savingsTransactionEntityForBeneficiaryAccount = savingTransactionRepository.save(((SavingsTransactionEntity) map.get("savingsTransactionEntityForBeneficiaryAccount")));

							//send mail condition
							if(currentsTransactionEntityForSenderAccount!=null && savingsTransactionEntityForBeneficiaryAccount!=null) {
								mailResponse = ", Email Alert : "+sendEmailOnSuccessfulTransaction(senderCustomerId,transferRequestDTO,transactionResponse,senderTotalBalance);
							}
							
							updateBudgetLimitByTransferAmount(budget,jsonNode,transferRequestDTO.getTransferAmount(),transferRequestDTO);
							
						} else if (map.get("transferRequestType") != null&& map.get("transferRequestType").equals("curToCur")) {

							log.info("Transferring amount from currents account: {} to another currents account: {}",transferRequestDTO.getFromAccountNumber(),transactionResponse.getData().getAccountnumber());
							CurrentsTransactionEntity currentsTransactionEntityForSenderAccount = currentsTransactionRepository.save(((CurrentsTransactionEntity) map.get("currentsTransactionEntityForSenderAccount")));
							CurrentsTransactionEntity currentsTransactionEntityForBeneficiaryAccount = currentsTransactionRepository.save(((CurrentsTransactionEntity) map.get("currentsTransactionEntityForBeneficiaryAccount")));
							
							//send mail condition
							if(currentsTransactionEntityForSenderAccount!=null && currentsTransactionEntityForBeneficiaryAccount!=null) {
								mailResponse = " , Email Alert :"+ sendEmailOnSuccessfulTransaction(senderCustomerId,transferRequestDTO,transactionResponse,senderTotalBalance);
							}
							
							updateBudgetLimitByTransferAmount(budget,jsonNode,transferRequestDTO.getTransferAmount(),transferRequestDTO);
							
						}
						
					} else if (transactionResponse.getData().getAccountstatus().equals("CREDITBLOCKED")) {
						transactionEntityTransformer(transactionId,"FAILED","NET BANKING");
						return new TransactionResponse(null,"Transaction Failed!!! The destination account number: "+ transactionResponse.getData().getAccountnumber()+ " is blocked for all CREDIT transactions!!!",404, "ERR-RBA-100CB-DEST");
				  }
				} // for loop ends
		} else {
			TransactionResponse transactionResponse = new TransactionResponse(null,"Transfer request failed as one of the beneficiary account doesn't exist!!!", 404,"ERR-RBA-500");
			transactionEntityTransformer(transactionId,"FAILED","NET BANKING");
			return transactionResponse;
		}
	}
	
	String message = String.format("Transfer of amount successful to the requested account!!!");
	return new TransactionResponse(null, message+budgetResponse + mailResponse, 200, null);
}// method ends
	// send email functionality
	private String sendEmailOnSuccessfulTransaction(int senderCustomerId, TransferRequestDTO transferRequestDTO,
			TransactionResponse transactionResponse, double senderTotalBalance) {

		String urlsender = "http://localhost:1011/api/retailbanking/customer/getCustomerDetails/" + senderCustomerId;

		String urlRece = "http://localhost:1011/api/retailbanking/customer/getCustomerDetails/"
				+ transactionResponse.getData().getCustomerid();

		ResponseEntity<CustomerDTO> responseEntitySender = restTemplate.getForEntity(urlsender, CustomerDTO.class);

		ResponseEntity<CustomerDTO> responseEntityRece = restTemplate.getForEntity(urlRece, CustomerDTO.class);

		// Email service inputs
		String subject = "Maveric Bank Alerts";

		LocalDate currentDate = LocalDate.now();

		// mail to receive account
		String bodyRece = String.format(
				"Dear %s,\n\n" + "Rs. %s has been credited to your account: %s from account: %s" + " on %s. \n"
						+ "Available Balance is : %s\n\n"
						+ "Please reach out to customer care if this transaction was not authorized by you.\n\n"
						+ "Best regards,\n" + "Retail Banking Team",
				responseEntityRece.getBody().getFirstName(), transferRequestDTO.getTransferAmount(),
				transactionResponse.getData().getAccountnumber(), transferRequestDTO.getFromAccountNumber(),
				currentDate, transactionResponse.getData().getTotalbalance() + transferRequestDTO.getTransferAmount());

		// mail to receive account
		String bodySender = String.format(
				"Dear %s,\n\n" + "Rs. %s has been debited from your account: %s to account: %s" + " on %s. \n"
						+ "Available Balance is : %s\n\n"
						+ "Please reach out to customer care if this transaction was not authorized by you.\n\n"
						+ "Best regards,\n" + "Retail Banking Team",
				responseEntitySender.getBody().getFirstName(), transferRequestDTO.getTransferAmount(),
				transferRequestDTO.getFromAccountNumber(), transactionResponse.getData().getAccountnumber(),
				currentDate, senderTotalBalance);

		String urlEmail = "http://localhost:1014/email/sendEmail";

		String mailResponse = "";

		checkLowbalance(transferRequestDTO.getFromAccountNumber(),senderTotalBalance, responseEntitySender.getBody().getEmail());
		
		try {
			EmailRequest requestToSender = new EmailRequest(responseEntitySender.getBody().getEmail(), subject,
					bodySender);
			ResponseEntity<String> responseForSender = restTemplate.postForEntity(urlEmail, requestToSender,
					String.class);

			EmailRequest requestToRece = new EmailRequest(responseEntityRece.getBody().getEmail(), subject, bodyRece);
			ResponseEntity<String> responseForRece = restTemplate.postForEntity(urlEmail, requestToRece, String.class);

			if (responseForRece.getStatusCode().is2xxSuccessful()
					&& responseForSender.getStatusCode().is2xxSuccessful()) {
				mailResponse = "Email has been sent successfully to mail Ids of both the accounts involved : "
						+ responseEntityRece.getBody().getEmail() + " and " + responseEntitySender.getBody().getEmail();
				return mailResponse;
			} else {
				if (responseForRece.getStatusCode().is2xxSuccessful()) {
					mailResponse = "Email has been sent successfully to mail Id: "
							+ responseEntityRece.getBody().getEmail();
					if (!responseForSender.getStatusCode().is2xxSuccessful()) {
						return "Failed to send email.";
					}
				} else {
					return "Failed to send email.";
				}
				return mailResponse;
			}
		} catch (RestClientException ex) {
			ex.printStackTrace();
			return "Failed to send email. Please try again later.";
		}

	}

	private void updateBudgetLimitByTransferAmount(Budget budget, JsonNode jsonNode, double transferAmount,
			TransferRequestDTO transferRequestDTO) throws JsonProcessingException {

		JsonNode monthData = jsonNode.get(String.valueOf(LocalDate.now().getMonth().getValue()));
		JsonNode categoryBudgetExpense = monthData
				.get(transferRequestDTO.getTransactionCategory().toString().toLowerCase() + "_expense");

		ObjectMapper objectMapper = new ObjectMapper();
		((ObjectNode) monthData).put(transferRequestDTO.getTransactionCategory().toString().toLowerCase() + "_expense",
				categoryBudgetExpense.asDouble() + transferAmount);

		budget.setBudgetData(objectMapper.writeValueAsString(jsonNode));
		budgetRepository.save(budget);
	}

	@Override
	public BudgetSetupResponse getBudgetLimitByExpenseCategory(long accountNumber) {

		Budget budget = budgetRepository.findByAccountNumber(accountNumber);
		String budgetDataJson = "";

		if (budget != null) {
			budgetDataJson = budget.getBudgetData();
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, JsonNode> categoryThresholdValueMap = new HashMap<>();

			try {
				JsonNode jsonNode = objectMapper.readTree(budgetDataJson);

				LocalDate currentDate = LocalDate.now();
				int currentMonthValue = currentDate.getMonthValue();

				JsonNode jsonNodeForCurrentMonth = jsonNode.get(String.valueOf(currentMonthValue));

				if (jsonNodeForCurrentMonth != null && jsonNodeForCurrentMonth.isObject()) {
					Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNodeForCurrentMonth.fields();
					while (fieldsIterator.hasNext()) {
						Map.Entry<String, JsonNode> field = fieldsIterator.next();
						String fieldName = field.getKey();
						JsonNode fieldValue = field.getValue();

						if (fieldName.contains("_threshold")) {
							categoryThresholdValueMap.put(fieldName, fieldValue);
						}
					}
				} else {
					return new BudgetSetupResponse(404, "Invalid JSON...", null);
				}
			} catch (IOException e) {
				return new BudgetSetupResponse(500, e.getMessage(), null);
			}
			return new BudgetSetupResponse(200, "Expense wise budgets found for requested account number!!!",
					categoryThresholdValueMap);

		} else {
			return new BudgetSetupResponse(404, "Expense wise budgets not found for requested account number!!!", null);
		}
	}

	@Override
	public BudgetSetupResponse updateBudgetLimitByAccountNumber(long accountNumber,
			@Valid BudgetSetupDTO budgetSetupDTO) {

		Budget budget = budgetRepository.findByAccountNumber(accountNumber);
		JsonNode jsonNode;
		JsonNode jsonNodeForCurrentMonth;
		JsonNode jsonNodeFromCurrentMOnthTillEndYear = null;
		String budgetDataJson = "";
		int currentMonthNumber = budgetSetupDTO.getMonth();
		
		if (budget != null) {
			budgetDataJson = budget.getBudgetData();
			ObjectMapper objectMapper = new ObjectMapper();

			try {
				jsonNode = objectMapper.readTree(budgetDataJson);
				jsonNodeForCurrentMonth = jsonNode.get(String.valueOf(currentMonthNumber));
				if (jsonNodeForCurrentMonth != null && jsonNodeForCurrentMonth.isObject()) {
					for (Map.Entry<TransactionCategory, Double> entry : budgetSetupDTO.getBudgetThreshold()
							.entrySet()) {
						String jsonKey = entry.getKey().toString().concat("_threshold");

						for (int i = currentMonthNumber; i <= 12; i++) {
							jsonNodeFromCurrentMOnthTillEndYear = jsonNode.get(String.valueOf(i));
							((ObjectNode) jsonNodeFromCurrentMOnthTillEndYear).put(jsonKey, entry.getValue()); // setting
																												// the
																												// budget
						}
					}
				} else {
					return new BudgetSetupResponse(404, "Invalid JSON...", null);
				}
			} catch (IOException e) {
				return new BudgetSetupResponse(500, e.getMessage(), null);
			}

			try {
				budget.setBudgetData(objectMapper.writeValueAsString(jsonNode));
			} catch (JsonProcessingException e) {
				return new BudgetSetupResponse(500, e.getMessage(), null);
			}
			budgetRepository.save(budget); // save

			return new BudgetSetupResponse(200, String.format(
					"Budgets have been set for the requested expense categories from current month: %s, till end of the year",
					LocalDate.now().getMonth()), null);
		} else {
			return new BudgetSetupResponse(404, "Expense wise budgets not found for requested account number!!!", null);
		}
	}

	/* Filtering active account of the user from list of the account */
	public List<Accounts> findAllAccount(List<Accounts> list) {
		log.info("Inside the findAllAccount service with input: {}", list);
		List<Accounts> activeAccounts = list.stream()
				.filter(account -> "Active".equalsIgnoreCase(account.getAccountstatus())).collect(Collectors.toList());
		log.info("End of findAllAccount service. Active accounts: {}", activeAccounts);
		return activeAccounts;
	}

	/*
	 * Funtion to check low balance of the account and send the mail if it is lower
	 * then 1000 returns True if the acount has low balance i.e <1000 returns false
	 * if account has more then 1000 i.e >1000
	 * 
	 */
	public boolean checkLowbalance(long accountNumber, double balance, String tomail) {
		log.info("Inside the fuction to check low balance");
		double lowBalance = 1000.00;

		if (balance < lowBalance) {
			log.info("Checking low balance inside the function and low balance found");

			String subject = "Low balance notification";

			String body = String.format("Dear Customer,\n\n"
					+ "We regret to inform you that your account balance for account number %d has fallen below the minimum threshold.\n"
					+ "Current Account Balance: \u20B9 %.2f\n\n"
					+ "We kindly request that you take the necessary steps to replenish your account funds.\n\n"
					+ "Best Regards,\n" + "Retail Banking Team", accountNumber, balance);

			String url = "http://localhost:1014/email/sendEmail";

			String mailResponse = "";
			EmailRequest request = new EmailRequest(tomail, subject, body);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				mailResponse = "Email has been sent successfully to mail Id :" + tomail;
				log.info("Mail has been sent successfully regarding low balanceto mail id " + tomail);
			} else {
				mailResponse = "Failed to send email.";
				log.info("Failed to send the mail to mail id " + tomail);
			}
			log.info("End of checking low balamce function and returned low balance for account number "
					+ accountNumber);

			return true;
		} else {
			log.info("End of checking low balamce function and returned not as low balance for account number "
					+ accountNumber);
			return false;
		}
	}

	@Override
	public List<BenificiaryAccount> getBenificiaryByCustomerId(int customerId) {
		return benificiaryAccountRepository.findByCustomerid(customerId);
	}

	/**
	 * @author nirajku
	 * 
	 *         Service to add Benificiary If account number exisits in account table
	 *         then only it will add Resttemplate calling Account Microservice
	 */
 
	@Override
	public BenificiaryResponse addBeneficiary(AddBenificiaryDTO addBenificiaryDTO) {
		log.info("Inside the SeviceImpl of adding benificiary ");
 
		BenificiaryResponse response = new BenificiaryResponse();
//				ResponseEntity<String> accountResponse = null;
//		try {
			String url = "http://localhost:1012/api/retailbanking/accounts/fetchAccountStatus/"
					+ addBenificiaryDTO.getAccountNumber();
			ResponseEntity<String> accountResponse = restTemplate.getForEntity(url, String.class);
 
			// If response entity in Not Ok then it will go in Catch as account doesn't
			// exist
			log.info("Inside if of try block as account number exist");
			if (!benificiaryAccountRepository.existsByBenaccountnumberAndCustomerid(
					addBenificiaryDTO.getAccountNumber(), addBenificiaryDTO.getCustomerId())) {
				log.info("Benificiary not added already, so adding it now with account number "
						+ addBenificiaryDTO.getAccountNumber());
 
				BenificiaryAccount benificiaryAccount = new BenificiaryAccount();
				benificiaryAccount.setCustomerid(addBenificiaryDTO.getCustomerId());
				benificiaryAccount.setBenaccountnumber(addBenificiaryDTO.getAccountNumber());
				benificiaryAccount.setBen_name(addBenificiaryDTO.getName());
				benificiaryAccount.setBen_email(addBenificiaryDTO.getEmail());
 
				benificiaryAccountRepository.save(benificiaryAccount);
 
				response.setStatus(200);
				response.setMessage("Beneficiary added successfully");
				log.info("Ending service with the message Benificiary added successfully with account number "
						+ addBenificiaryDTO.getAccountNumber());
 
				return response;
			} else {
				response.setStatus(400);
				response.setMessage("Beneficiary already added");
				log.info("Ending service with the message Benificiary added already, so can't add again ");
 
				return response;
			}
//		}
 
//		catch (Exception e) {
//			e.getStackTrace();
//			log.info("Inside catch block as account number does not exist");
//			response.setStatus(404);
//			response.setMessage("Account number does not exist");
//			log.info("Ending service with the message Account number doesn't Exist ");
// 
//			return response;
//		}
 
	}
	
	@Override
	public ExpenseCategoryResponse getMonthlyExpensesByCategoryAndAccountNumberAndAccountType(Long accountNumber, String accountType,
			TransactionCategory expenseCategory) {
 
		LocalDateTime now = LocalDateTime.now();
 
		// Start of the current month
		LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
 
		// End of the current month
		LocalDateTime endOfMonth = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear()))
				.withHour(23).withMinute(59).withSecond(59);
 
		Map<String, Double> dailyExpenseForCurrentMonth = null;
 
		if (StringUtils.equalsIgnoreCase(accountType, "SAVINGS")) {
			List<SavingsTransactionEntity> categorywiseTransactions = savingTransactionRepository
					.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
							accountNumber, startOfMonth, endOfMonth, expenseCategory);
 
			dailyExpenseForCurrentMonth = calculateDailyExpenseForCategorySavings(categorywiseTransactions,
					expenseCategory);
 
			log.info(dailyExpenseForCurrentMonth.toString());
 
		} else if (StringUtils.equalsIgnoreCase(accountType, "CURRENT")) {
			List<CurrentsTransactionEntity> categorywiseTransactions = currentsTransactionRepository
					.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
							accountNumber, startOfMonth, endOfMonth, expenseCategory);
 
			log.info(categorywiseTransactions.toString());
 
			dailyExpenseForCurrentMonth = calculateDailyExpenseForCategoryCurrent(categorywiseTransactions,
					expenseCategory);
 
			log.info(dailyExpenseForCurrentMonth.toString());
 
		} else {
			throw new RuntimeException("Invalid account type");
		}
 
		// Convert Map<String, Double> to Map<String, Object>
		Map<String, Object> expenseData = new HashMap<>();
		
		for (Map.Entry<String, Double> entry : dailyExpenseForCurrentMonth.entrySet()) {
			expenseData.put(entry.getKey(), entry.getValue());
		}
		
		ExpenseCategoryResponse expenseCategoryResponse = new ExpenseCategoryResponse();
		expenseCategoryResponse.setMessage(
				String.format("Total daily expense towards %s for current month.", expenseCategory.toString()));
		expenseCategoryResponse.setStatus(200);
		expenseCategoryResponse.setData(expenseData);
		
		return expenseCategoryResponse;
 
	}
 
	private Map<String, Double> calculateDailyExpenseForCategoryCurrent(List<CurrentsTransactionEntity> transactions,
			TransactionCategory category) {
 
		// Group transactions by date
		Map<LocalDate, List<CurrentsTransactionEntity>> transactionsByDate = transactions.stream()
				.filter(transaction -> transaction.getTransactionCategory() == category)
				.collect(Collectors.groupingBy(transaction -> transaction.getTransactionTime().toLocalDate()));
 
		// Calculate total expense for each day
		Map<String, Double> dailyExpenseMap = new HashMap<>();
		for (LocalDate date : transactionsByDate.keySet()) {
			List<CurrentsTransactionEntity> transactionsForDay = transactionsByDate.get(date);
			double totalExpense = transactionsForDay.stream()
					.mapToDouble(CurrentsTransactionEntity::getTransactionAmount).sum();
			dailyExpenseMap.put(date.toString(), totalExpense);
		}
 
		return dailyExpenseMap;
	}
 
	private Map<String, Double> calculateDailyExpenseForCategorySavings(List<SavingsTransactionEntity> transactions,
			TransactionCategory category) {
 
		// Group transactions by date
		Map<LocalDate, List<SavingsTransactionEntity>> transactionsByDate = transactions.stream()
				.filter(transaction -> transaction.getTransactionCategory() == category)
				.collect(Collectors.groupingBy(transaction -> transaction.getTransactionTime().toLocalDate()));
 
		// Calculate total expense for each day
		Map<String, Double> dailyExpenseMap = new HashMap<>();
		for (LocalDate date : transactionsByDate.keySet()) {
			List<SavingsTransactionEntity> transactionsForDay = transactionsByDate.get(date);
			double totalExpense = transactionsForDay.stream()
					.mapToDouble(SavingsTransactionEntity::getTransactionAmount).sum();
			dailyExpenseMap.put(date.toString(), totalExpense);
		}
 
		return dailyExpenseMap;
	}

	@Override
	public BudgetSetupResponse addBudgetLimitByAccountNumber(long accountNumber) {
		
	    BudgetSetupResponse response = new BudgetSetupResponse();
	    try {
	        Budget budget = new Budget();
	        budget.setAccountNumber(accountNumber);
	        String data = "{\"1\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"2\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"3\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"4\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"5\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"6\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"7\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"8\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"9\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"10\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"11\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0},\"12\":{\"food_expense\":0.0,\"fuel_expense\":0.0,\"loan_expense\":0.0,\"bills_expense\":0.0,\"food_threshold\":0.0,\"fuel_threshold\":0.0,\"loan_threshold\":0.0,\"travel_expense\":0.0,\"bills_threshold\":0.0,\"recharge_expense\":0.0,\"shopping_expense\":0.0,\"travel_threshold\":0.0,\"recharge_threshold\":0.0,\"shopping_threshold\":0.0,\"entertainment_expense\":0.0,\"miscellaneous_expense\":0.0,\"entertainment_threshold\":0.0,\"miscellaneous_threshold\":0.0}}";
	        budget.setBudgetData(data);
	        budgetRepository.save(budget);

	        response.setStatus(200);
	        response.setMessage("Budget limit added successfully");
	        response.setData(null);
	        
	    } catch (Exception e) {
	        response.setMessage("Failed to add budget limit");
	        response.setStatus(500);
	        response.setData(null);

	    }

	    return response;
	}
}