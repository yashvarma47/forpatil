package com.thinknxt.rba.services;

import java.time.LocalDateTime;
import java.util.List;

import com.thinknxt.rba.response.TransactionDTOResponse;

public interface SavingsTransactionService {

	List<TransactionDTOResponse> getLatestTransactionsByAccountNumber(Long accountNumber, String accountType);

	List<TransactionDTOResponse> getMonthlyTransactionsByAccountNumber(Long accountNumber, String accountType, int year,
			int month);

	List<TransactionDTOResponse> getQuarterlyTransactionsByAccountNumber(Long accountNumber, String accountType, int year,
			int quarter);

	List<TransactionDTOResponse> getYearlyTransactionsByAccountNumber(Long accountNumber, String accountType, int year);

	public List<TransactionDTOResponse> getTransactionsBetweenTwoDatesByAccountNumber(Long accountNumber,
			String accountType, LocalDateTime startDate, LocalDateTime endDate);
}
