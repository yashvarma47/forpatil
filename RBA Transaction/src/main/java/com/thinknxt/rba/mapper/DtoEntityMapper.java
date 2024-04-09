package com.thinknxt.rba.mapper;
 
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
 
import com.thinknxt.rba.dto.TransferRequestDTO;
import com.thinknxt.rba.entities.CurrentsTransactionEntity;
import com.thinknxt.rba.entities.SavingsTransactionEntity;
import com.thinknxt.rba.entities.TransactionEntity;
import com.thinknxt.rba.response.TransactionResponse;
import com.thinknxt.rba.utils.TransactionCategory;
 
@Component
public class DtoEntityMapper {
 
	private final RestTemplate restTemplate;
 
	
	@Autowired
	public DtoEntityMapper(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
 
	String transferRequestType = "";
	SavingsTransactionEntity beneficiarySavingsEntity = null;
	SavingsTransactionEntity senderSavingsEntity = null;
	CurrentsTransactionEntity beneficiaryCurrentsEntity = null;
	CurrentsTransactionEntity senderCurrentsEntity = null;

 
	/**
     * This method maps TransferRequestDTO and TransactionResponse to corresponding entity classes
     * (SavingsTransactionEntity or CurrentsTransactionEntity) based on the account types.
     * Handles the transfer of money within accounts and updates account balances.
     *
     * @param transactionResponse Response containing account details of the beneficiary
     * @param transferRequestDTO  Request containing details of the money transfer
     * @param senderTotalBalance  Total balance of the sender's account
     * @param senderCustomerId    Customer ID of the sender
     * @param transactionId       Unique ID for the transaction
     * @param senderAccountType   Account type of the sender (SAVINGS or CURRENT)
     * @param benefAccountType    Account type of the beneficiary (SAVINGS or CURRENT)
     * @return Map containing transaction entities and types
     */
	public Map<String, Object> accountToAccountTransactionDTOtoEntity(
			TransactionResponse transactionResponse, TransferRequestDTO transferRequestDTO, double senderTotalBalance,
			int senderCustomerId, String transactionId, String senderAccountType, String benefAccountType) {
 
		
		double netAmountBenef = transactionResponse.getData().getTotalbalance()+ transferRequestDTO.getTransferAmount();
		double netAmountSender = senderTotalBalance - transferRequestDTO.getTransferAmount();
 
		Map<String, Object> map = new HashMap<>();
		if (senderAccountType.equalsIgnoreCase("SAVINGS") && benefAccountType.equalsIgnoreCase("SAVINGS")) {
			transferRequestType = "savToSav";
 
			 beneficiarySavingsEntity = createSavingsTransactionEntity(
					transactionResponse.getData().getAccountnumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId,
					transactionResponse.getData().getCustomerid(), transferRequestDTO.getFromAccountNumber(), "Credit",
					netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
 
			 senderSavingsEntity = createSavingsTransactionEntity(
					transferRequestDTO.getFromAccountNumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId, senderCustomerId,
					transactionResponse.getData().getAccountnumber(), "Debit", netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
			 	map.put("savingsTransactionEntityForBeneficiaryAccount", beneficiarySavingsEntity);
				map.put("savingsTransactionEntityForSenderAccount", senderSavingsEntity);
				map.put("transactionEntity",senderSavingsEntity.getTransaction());
				map.put("transferRequestType", transferRequestType);

		} else if (senderAccountType.equalsIgnoreCase("CURRENT") && benefAccountType.equalsIgnoreCase("CURRENT")) {
			transferRequestType = "curToCur";
			 beneficiaryCurrentsEntity = createCurrentsTransactionEntity(
					transactionResponse.getData().getAccountnumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId,
					transactionResponse.getData().getCustomerid(), transferRequestDTO.getFromAccountNumber(), "Credit",
					netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
 
			 senderCurrentsEntity = createCurrentsTransactionEntity(
					transferRequestDTO.getFromAccountNumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId, senderCustomerId,
					transactionResponse.getData().getAccountnumber(), "Debit", netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
			 	map.put("currentsTransactionEntityForSenderAccount", senderCurrentsEntity);
				map.put("currentsTransactionEntityForBeneficiaryAccount", beneficiaryCurrentsEntity);
				map.put("transactionEntity",senderCurrentsEntity.getTransaction());
				map.put("transferRequestType", transferRequestType);
		} else if (senderAccountType.equalsIgnoreCase("SAVINGS") && benefAccountType.equalsIgnoreCase("CURRENT")) {
			transferRequestType = "savToCur";
			 beneficiaryCurrentsEntity = createCurrentsTransactionEntity(
					transactionResponse.getData().getAccountnumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId,
					transactionResponse.getData().getCustomerid(), transferRequestDTO.getFromAccountNumber(), "Credit",
					netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
 
			 senderSavingsEntity = createSavingsTransactionEntity(
					transferRequestDTO.getFromAccountNumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId, senderCustomerId,
					transactionResponse.getData().getAccountnumber(), "Debit", netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
			 	map.put("savingsTransactionEntityForSenderAccount", senderSavingsEntity);
				map.put("currentsTransactionEntityForBeneficiaryAccount", beneficiaryCurrentsEntity);
				map.put("transactionEntity",senderSavingsEntity.getTransaction());
				map.put("transferRequestType", transferRequestType);
		} else if (senderAccountType.equalsIgnoreCase("CURRENT") && benefAccountType.equalsIgnoreCase("SAVINGS")) {
			transferRequestType = "curToSav";
			 beneficiarySavingsEntity = createSavingsTransactionEntity(
					transactionResponse.getData().getAccountnumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId,
					transactionResponse.getData().getCustomerid(), transferRequestDTO.getFromAccountNumber(), "Credit",
					netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
 
			 senderCurrentsEntity = createCurrentsTransactionEntity(
					transferRequestDTO.getFromAccountNumber(), transferRequestDTO.getTransferAmount(),
					transferRequestDTO.getTransactionNote(), transactionId, senderCustomerId,
					transactionResponse.getData().getAccountnumber(), "Debit", netAmountBenef, netAmountSender,transferRequestDTO.getTransactionCategory());
			 	map.put("currentsTransactionEntityForSenderAccount", senderCurrentsEntity);
				map.put("savingsTransactionEntityForBeneficiaryAccount", beneficiarySavingsEntity);
				map.put("transactionEntity",senderCurrentsEntity.getTransaction());
				map.put("transferRequestType", transferRequestType);
		}
 
		String urlBenef = buildUpdateAmountUrl(netAmountBenef, transactionResponse.getData().getAccountnumber());
		String urlSender = buildUpdateAmountUrl(netAmountSender, transferRequestDTO.getFromAccountNumber());
 
		performPostRequest(urlSender, netAmountSender, transferRequestDTO.getFromAccountNumber());
		performPostRequest(urlBenef, netAmountBenef, transactionResponse.getData().getAccountnumber());
		return map;
	}
 
	private SavingsTransactionEntity createSavingsTransactionEntity(Long accountNumber, double transactionAmount,
			String transactionNote, String transactionId, int customerId, Long recipient, String transactionType,
			double netAmountBenef, double netAmountSender,TransactionCategory transactionCategory) {
		SavingsTransactionEntity entity = new SavingsTransactionEntity();
		entity.setAccountNumber(accountNumber);
		entity.setTransactionAmount(transactionAmount);
		entity.setTransactionTime(LocalDateTime.now());
		entity.setNarratives(transactionNote);
		entity.setTransactionId(transactionId);
		entity.setCustomerId(customerId);
		entity.setRecipient(recipient);
		entity.setRemainingBalance(calculateRemainingBalance(transactionType, netAmountBenef, netAmountSender));
		entity.setTransactionType(transactionType);
		entity.setTransactionCategory(transactionCategory);
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setTransactionId(transactionId);
		transactionEntity.setTransactionStatus("SUCCESS");
		transactionEntity.setTransactionTime(LocalDateTime.now());
		transactionEntity.setTransactionMode("NET BANKING");
 
		entity.setTransaction(transactionEntity);
 
		return entity;
	}
 
	private CurrentsTransactionEntity createCurrentsTransactionEntity(Long accountNumber, double transactionAmount,
			String transactionNote, String transactionId, int customerId, Long recipient, String transactionType,
			double netAmountBenef, double netAmountSender,TransactionCategory transactionCategory) {
		CurrentsTransactionEntity entity = new CurrentsTransactionEntity();
		entity.setAccountNumber(accountNumber);
		entity.setTransactionAmount(transactionAmount);
		entity.setTransactionTime(LocalDateTime.now());
		entity.setNarratives(transactionNote);
		entity.setTransactionId(transactionId);
		entity.setCustomerId(customerId);
		entity.setRecipient(recipient);
		entity.setRemainingBalance(calculateRemainingBalance(transactionType, netAmountBenef, netAmountSender));
		entity.setTransactionType(transactionType);
		entity.setTransactionCategory(transactionCategory);
 
 
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setTransactionId(transactionId);
		transactionEntity.setTransactionStatus("SUCCESS");
		transactionEntity.setTransactionTime(LocalDateTime.now());
		transactionEntity.setTransactionMode("NET BANKING");
 
		entity.setTransaction(transactionEntity);
 
		return entity;
	}
 
	private String buildUpdateAmountUrl(double netAmount, Long accountNumber) {
		return "http://localhost:1012/api/retailbanking/accounts/updateAmount?newAmount=" + netAmount
				+ "&accountNumber=" + accountNumber;
	}
 
	private ResponseEntity<Void> performPostRequest(String url, double netAmount, Long accountNumber) {
		MultiValueMap<String, Object> requestParams = new LinkedMultiValueMap<>();
		requestParams.add("newAmount", netAmount);
		requestParams.add("accountNumber", accountNumber);
		ResponseEntity<Void> responseEntity = restTemplate.postForEntity(url, requestParams, Void.class);
		return responseEntity;
	}
 
	private double calculateRemainingBalance(String transactionType, double netAmountBenef, double netAmountSender) {
		return transactionType.equals("Credit") ? netAmountBenef : netAmountSender;
	}
}