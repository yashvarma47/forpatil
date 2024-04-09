package com.thinknxt.rba.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinknxt.rba.entities.SavingsTransactionEntity;
import com.thinknxt.rba.repository.SavingsTransactionRepository;
import com.thinknxt.rba.response.ExpenseCategoryResponse;
import com.thinknxt.rba.response.TransactionDTOResponse;
import com.thinknxt.rba.utils.DayTransaction;
import com.thinknxt.rba.utils.TransactionCategory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SavingsTransactionServiceImpl implements SavingsTransactionService {

	@Autowired
	private SavingsTransactionRepository savingsTransactionRepository;

	public List<TransactionDTOResponse> getLatestTransactionsByAccountNumber(Long accountNumber, String accountType) {
		if ("savings".equalsIgnoreCase(accountType)) {
			// Retrieve the latest currents transactions
			List<SavingsTransactionEntity> latestCurrentsTransactions = savingsTransactionRepository
					.findTop10ByAccountNumber(accountNumber);

			// Map the currents transactions to transaction responses
			List<TransactionDTOResponse> transactionDTOResponses = new ArrayList<>();
			for (SavingsTransactionEntity currentsTransaction : latestCurrentsTransactions) {
				TransactionDTOResponse transactionDTOResponse = mapFromCurrentsTransaction(currentsTransaction);
				transactionDTOResponses.add(transactionDTOResponse);
			}
			return transactionDTOResponses;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}

	@Override
	public List<TransactionDTOResponse> getMonthlyTransactionsByAccountNumber(Long accountNumber, String accountType,
			int year, int month) {
		if ("savings".equalsIgnoreCase(accountType)) {
			// Retrieve the monthly savings transactions
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
			LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
			List<SavingsTransactionEntity> monthlySavingsTransactions = savingsTransactionRepository
					.findByAccountNumberAndTransactiontimebetweenOrderByTransactionTimeDesc(accountNumber, startOfMonth,
							endOfMonth);

			// Map the monthly savings transactions to transaction responses
			List<TransactionDTOResponse> transactionDTOResponses = new ArrayList<>();
			for (SavingsTransactionEntity savingsTransaction : monthlySavingsTransactions) {
				TransactionDTOResponse transactionDTOResponse = mapFromSavingsTransaction(savingsTransaction);
				transactionDTOResponses.add(transactionDTOResponse);
			}
			return transactionDTOResponses;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}

	// Utility method to map SavingsTransaction to TransactionResponse
	private TransactionDTOResponse mapFromSavingsTransaction(SavingsTransactionEntity savingsTransaction) {
		TransactionDTOResponse transactionDTOResponse = new TransactionDTOResponse();
		transactionDTOResponse.setId(savingsTransaction.getId());
		transactionDTOResponse.setCustomerId(savingsTransaction.getCustomerId());
		transactionDTOResponse.setAccountNumber(savingsTransaction.getAccountNumber());
		transactionDTOResponse.setTransactionId(savingsTransaction.getTransactionId());
		transactionDTOResponse.setTransactionAmount(savingsTransaction.getTransactionAmount());
		transactionDTOResponse.setTransactionType(savingsTransaction.getTransactionType());
		transactionDTOResponse.setRemainingBalance(savingsTransaction.getRemainingBalance());
		transactionDTOResponse.setTransactionTime(savingsTransaction.getTransactionTime());
		transactionDTOResponse.setRecipient(savingsTransaction.getRecipient());
		transactionDTOResponse.setNarratives(savingsTransaction.getNarratives());
		transactionDTOResponse.setTransactionCategory(savingsTransaction.getTransactionCategory());
		return transactionDTOResponse;
	}

	public static TransactionDTOResponse mapFromCurrentsTransaction(SavingsTransactionEntity savingsTransaction) {
		// Your mapping logic here
		TransactionDTOResponse transactionDTOResponse = new TransactionDTOResponse();
		transactionDTOResponse.setId(savingsTransaction.getId());
		transactionDTOResponse.setCustomerId(savingsTransaction.getCustomerId());
		transactionDTOResponse.setAccountNumber(savingsTransaction.getAccountNumber());
		transactionDTOResponse.setTransactionId(savingsTransaction.getTransactionId());
		transactionDTOResponse.setTransactionAmount(savingsTransaction.getTransactionAmount());
		transactionDTOResponse.setTransactionType(savingsTransaction.getTransactionType());
		transactionDTOResponse.setRemainingBalance(savingsTransaction.getRemainingBalance());
		transactionDTOResponse.setTransactionTime(savingsTransaction.getTransactionTime());
		transactionDTOResponse.setRecipient(savingsTransaction.getRecipient());
		transactionDTOResponse.setNarratives(savingsTransaction.getNarratives());
		transactionDTOResponse.setTransactionCategory(savingsTransaction.getTransactionCategory());
		return transactionDTOResponse;
	}

	@Override
	public List<TransactionDTOResponse> getQuarterlyTransactionsByAccountNumber(Long accountNumber, String accountType,
			int year, int quarter) {
		if ("savings".equalsIgnoreCase(accountType)) {
			List<TransactionDTOResponse> quarterlyTransactions = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				int month = (quarter - 1) * 3 + i + 1;
				YearMonth startYearMonth = YearMonth.of(year, month);
				LocalDateTime startDate = startYearMonth.atDay(1).atStartOfDay();
				YearMonth endYearMonth = startYearMonth.plusMonths(1);
				LocalDateTime endDate = endYearMonth.atEndOfMonth().atTime(23, 59, 59);

				List<SavingsTransactionEntity> monthlySavingsTransactions = savingsTransactionRepository
						.findByAccountNumberAndTransactiontimebetweenOrderByTransactionTimeDesc(accountNumber,
								startDate, endDate);

				// Map the monthly savings transactions to transaction responses
				for (SavingsTransactionEntity savingsTransaction : monthlySavingsTransactions) {
					TransactionDTOResponse transactionDTOResponse = mapFromSavingsTransaction(savingsTransaction);
					quarterlyTransactions.add(transactionDTOResponse);
				}
				
			}
			return quarterlyTransactions;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}

	@Override
	public List<TransactionDTOResponse> getYearlyTransactionsByAccountNumber(Long accountNumber, String accountType,
			int year) {
		if ("savings".equalsIgnoreCase(accountType)) {
			List<TransactionDTOResponse> yearlyTransactions = new ArrayList<>();
			for (int month = 1; month <= 12; month++) {
				YearMonth yearMonth = YearMonth.of(year, month);
				LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
				LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
				List<SavingsTransactionEntity> monthlySavingsTransactions = savingsTransactionRepository
						.findByAccountNumberAndTransactiontimebetweenOrderByTransactionTimeDesc(accountNumber,
								startDate, endDate);

				for (SavingsTransactionEntity savingsTransaction : monthlySavingsTransactions) {
					TransactionDTOResponse transactionDTOResponse = mapFromSavingsTransaction(savingsTransaction);
					yearlyTransactions.add(transactionDTOResponse);
				}
			}
			return yearlyTransactions;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}

	
	public ExpenseCategoryResponse getYearlyTransactionsByAccountNumberAndExpenseCategory(Long accountNumber,
			String accountType, int year) {

		if ("savings".equalsIgnoreCase(accountType)) {
			
			List<Object[]> yearlySavingsTransactions = savingsTransactionRepository.
						findYearlySumExpenseByExpenseCategory(accountNumber,year);
			List<SavingsTransactionEntity>  yearlySavingsTransaction = null;
			List<SavingsTransactionEntity> yearlyTransactions = new ArrayList<>();
			
			
			Map<String, Object> transactionsByCatergory = new HashMap<>();
			
				for (Object[] row : yearlySavingsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
						
				    	LocalDateTime startDate = LocalDate.of(year, 1, 1).atStartOfDay();
				        LocalDateTime endDate = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
				        
						yearlySavingsTransaction = savingsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			                accountNumber, startDate, endDate, TransactionCategory.valueOf(transactionCategory));
						if(!yearlySavingsTransactions.isEmpty()) {
							yearlyTransactions.addAll(yearlySavingsTransaction);
							
						}
				    
				}
				
				List<String> listOfExpenseCategories = yearlyTransactions.stream().map(t->t.getTransactionCategory().toString()).distinct().collect(Collectors.toList());

				for(String str : listOfExpenseCategories) {
					transactionsByCatergory.put(str, yearlyTransactions.stream().filter(t->t.getTransactionCategory().toString().equals(str)).collect(Collectors.toList()));
				}
				
				ExpenseCategoryResponse expenseCategoryResponse = new ExpenseCategoryResponse();
				expenseCategoryResponse.setMessage("Total Balance for particular expense category for a requested year along with the transactions for the selected time period.");
				expenseCategoryResponse.setStatus(200);
				expenseCategoryResponse.setData(transactionsByCatergory);
				
			return expenseCategoryResponse;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}

	
	public ExpenseCategoryResponse getMonthlyTransactionsByAccountNumberAndExpenseCategory(Long accountNumber,
			String accountType, int year, int month) {
		
		if ("savings".equalsIgnoreCase(accountType)) {
			
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
			LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

			
			List<Object[]> monthlySavingsTransactions = savingsTransactionRepository.
						findMonthlySumExpenseByExpenseCategory(accountNumber, month, year);
			List<SavingsTransactionEntity>  monthlySavingsTransaction = null;
			List<SavingsTransactionEntity> monthlyTransactions = new ArrayList<>();

			
			Map<String, Object> transactionsByCatergory = new HashMap<>();
			
				for (Object[] row : monthlySavingsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
				    
						monthlySavingsTransaction = savingsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			                accountNumber, startOfMonth, endOfMonth, TransactionCategory.valueOf(transactionCategory));
						if(!monthlySavingsTransactions.isEmpty()) {
							monthlyTransactions.addAll(monthlySavingsTransaction);
							
						}
				}
				
				List<String> listOfExpenseCategories = monthlyTransactions.stream().map(t->t.getTransactionCategory().toString()).distinct().collect(Collectors.toList());

				for(String str : listOfExpenseCategories) {
					transactionsByCatergory.put(str, monthlyTransactions.stream().filter(t->t.getTransactionCategory().toString().equals(str)).collect(Collectors.toList()));
				}
				
				ExpenseCategoryResponse expenseCategoryResponse = new ExpenseCategoryResponse();
				expenseCategoryResponse.setMessage("Total Balance for particular expense category for a requested year along with the transactions for the selected time period.");
				expenseCategoryResponse.setStatus(200);
				expenseCategoryResponse.setData(transactionsByCatergory);
				
			return expenseCategoryResponse;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}

	
	
	public ExpenseCategoryResponse getWeeklyTransactionsByAccountNumberAndExpenseCategory(Long accountNumber,
			String accountType) {
		
		if ("savings".equalsIgnoreCase(accountType)) {
	        
			LocalDate currentDate = LocalDate.now();

	        // Calculate the start and end dates of the last week
	        LocalDate lastWeekStart = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	        LocalDate lastWeekEnd = lastWeekStart.plusDays(6); // Last day of the week is 6 days after the start

			List<Object[]> weeklySavingsTransactions = savingsTransactionRepository.
					findWeeklySumExpenseByExpenseCategory(accountNumber,lastWeekStart.atStartOfDay(), lastWeekEnd.atTime(23, 59, 59));
			List<SavingsTransactionEntity>  weeklySavingsTransaction = null;
			List<SavingsTransactionEntity> weeklyTransactions = new ArrayList<>();

			
			
			Map<String, Object> transactionsByCatergory = new HashMap<>();
			
				for (Object[] row : weeklySavingsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
				    
						weeklySavingsTransaction = savingsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
	                accountNumber, lastWeekStart.atStartOfDay(), lastWeekEnd.atTime(23, 59, 59), TransactionCategory.valueOf(transactionCategory));
				if(!weeklySavingsTransactions.isEmpty()) {
					weeklyTransactions.addAll(weeklySavingsTransaction);
					
				}
				}
				
				List<String> listOfExpenseCategories = weeklyTransactions.stream().map(t->t.getTransactionCategory().toString()).distinct().collect(Collectors.toList());

				for(String str : listOfExpenseCategories) {
					transactionsByCatergory.put(str, weeklyTransactions.stream().filter(t->t.getTransactionCategory().toString().equals(str)).collect(Collectors.toList()));
				}
				
				ExpenseCategoryResponse expenseCategoryResponse = new ExpenseCategoryResponse();
				expenseCategoryResponse.setMessage("Total Balance for particular expense category for a requested year along with the transactions for the selected time period.");
				expenseCategoryResponse.setStatus(200);
				expenseCategoryResponse.setData(transactionsByCatergory);
				
			return expenseCategoryResponse;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}


	public ExpenseCategoryResponse getYesterdayOrTodaysTransactionsByAccountNumberAndExpenseCategory(Long accountNumber,
			String accountType, DayTransaction date) {
		
		
		DayTransaction dayTransactionToday =  DayTransaction.today;
		DayTransaction dayTransactionYesterday =  DayTransaction.yesterday;
		
		if ("savings".equalsIgnoreCase(accountType)) {
	        
			 LocalDate today = LocalDate.now();
		
			 LocalDate yesterday = today.minusDays(1);
		        
			 List<SavingsTransactionEntity>  yesterdayOrTodaySavingsTransaction = null;
			 List<SavingsTransactionEntity> yesterdayOrTodayTransactions = new ArrayList<>();			
				
			 Map<String, Object> transactionsByCatergory = new HashMap<>();
				
			 
		if(dayTransactionYesterday.equals(date)) {
			
			
			List<Object[]> yesterdaySavingsTransactions = savingsTransactionRepository.
					findYesterdayOrTodaySumExpenseByExpenseCategory(accountNumber,LocalDateTime.of(yesterday, LocalTime.MIN),LocalDateTime.of(yesterday, LocalTime.MAX));
			
			
				for (Object[] row : yesterdaySavingsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
				    
				    yesterdayOrTodaySavingsTransaction = savingsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			                accountNumber, LocalDateTime.of(yesterday, LocalTime.MIN), LocalDateTime.of(yesterday, LocalTime.MAX), TransactionCategory.valueOf(transactionCategory));
						if(!yesterdaySavingsTransactions.isEmpty()) {
							yesterdayOrTodayTransactions.addAll(yesterdayOrTodaySavingsTransaction);
						}
				}
			 }
			 else if (dayTransactionToday.equals(date)) {
				 
					
				 List<Object[]> todaySavingsTransactions = savingsTransactionRepository.
							findYesterdayOrTodaySumExpenseByExpenseCategory(accountNumber,LocalDateTime.of(today, LocalTime.MIN),LocalDateTime.of(today, LocalTime.MAX));
					
					
						for (Object[] row : todaySavingsTransactions) {
						    Double totalBalance = (Double) row[0];
						    String transactionCategory = (String) row[1];
						    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
						    
								yesterdayOrTodaySavingsTransaction = savingsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
					                accountNumber, LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX), TransactionCategory.valueOf(transactionCategory));
								if(!todaySavingsTransactions.isEmpty()) {
									yesterdayOrTodayTransactions.addAll(yesterdayOrTodaySavingsTransaction);
								}
						}
				 
				 
				 
			 }
				List<String> listOfExpenseCategories = yesterdayOrTodayTransactions.stream().map(t->t.getTransactionCategory().toString()).distinct().collect(Collectors.toList());
				
				for(String str : listOfExpenseCategories) {
					transactionsByCatergory.put(str, yesterdayOrTodayTransactions.stream().filter(t->t.getTransactionCategory().toString().equals(str)).collect(Collectors.toList()));
				}
				
				ExpenseCategoryResponse expenseCategoryResponse = new ExpenseCategoryResponse();
				expenseCategoryResponse.setMessage("Total Balance for particular expense category for a requested year along with the transactions for the selected time period.");
				expenseCategoryResponse.setStatus(200);
				expenseCategoryResponse.setData(transactionsByCatergory);
				
			return expenseCategoryResponse;
		} else {
			throw new RuntimeException("Invalid account type");
		}

	}

	
	@Override
	public List<TransactionDTOResponse> getTransactionsBetweenTwoDatesByAccountNumber(Long accountNumber,
			String accountType, LocalDateTime startDate, LocalDateTime endDate) {
		if ("savings".equalsIgnoreCase(accountType)) {
			List<SavingsTransactionEntity>  rangedSavingsTransaction = savingsTransactionRepository
					.findByAccountNumberAndTransactiontimebetweenOrderByTransactionTimeDesc(accountNumber, startDate, endDate);
			
			List<TransactionDTOResponse> transactionDTOResponses = new ArrayList<>();
			for (SavingsTransactionEntity currentTransaction : rangedSavingsTransaction) {
				TransactionDTOResponse transactionDTOResponse = mapFromSavingsTransaction(currentTransaction);
				transactionDTOResponses.add(transactionDTOResponse);
			}
			return transactionDTOResponses;
		}else {
			throw new RuntimeException("Invalid account type");
		}
	}



}
