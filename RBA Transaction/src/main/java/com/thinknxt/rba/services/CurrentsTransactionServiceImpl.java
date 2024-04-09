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

import com.thinknxt.rba.entities.CurrentsTransactionEntity;
import com.thinknxt.rba.repository.CurrentsTransactionRepository;
import com.thinknxt.rba.response.ExpenseCategoryResponse;
import com.thinknxt.rba.response.TransactionDTOResponse;
import com.thinknxt.rba.utils.DayTransaction;
import com.thinknxt.rba.utils.TransactionCategory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CurrentsTransactionServiceImpl implements CurrentsTransactionService {

	@Autowired
	private CurrentsTransactionRepository currentsTransactionRepository;

	@Override
	public List<TransactionDTOResponse> getLatestTransactionsByAccountNumber(Long accountNumber, String accountType) {
		if ("current".equalsIgnoreCase(accountType)) {
			// Retrieve the latest currents transactions
			log.info("Fetching latest transactions for current account in the service layer");
			List<CurrentsTransactionEntity> latestCurrentsTransactions = currentsTransactionRepository
					.findTop10ByAccountNumber(accountNumber);

			log.info(latestCurrentsTransactions.toString());
			// Map the currents transactions to transaction responses
			List<TransactionDTOResponse> transactionDTOResponses = new ArrayList<>();
			for (CurrentsTransactionEntity currentsTransaction : latestCurrentsTransactions) {
				TransactionDTOResponse transactionDTOResponse = mapFromCurrentsTransaction(currentsTransaction);
				transactionDTOResponses.add(transactionDTOResponse);
			}
			log.info(transactionDTOResponses.toString());
			return transactionDTOResponses;
		} else {
			log.info("Account type is invalid in service layer");
			throw new RuntimeException("Invalid account type");
		}
	}

	// Utility method to map CurrentsTransaction to TransactionResponse
	private TransactionDTOResponse mapFromCurrentsTransaction(CurrentsTransactionEntity currentsTransaction) {
		TransactionDTOResponse transactionDTOResponse = new TransactionDTOResponse();
		transactionDTOResponse.setId(currentsTransaction.getId());
		transactionDTOResponse.setCustomerId(currentsTransaction.getCustomerId());
		transactionDTOResponse.setAccountNumber(currentsTransaction.getAccountNumber());
		transactionDTOResponse.setTransactionId(currentsTransaction.getTransactionId());
		transactionDTOResponse.setTransactionAmount(currentsTransaction.getTransactionAmount());
		transactionDTOResponse.setTransactionType(currentsTransaction.getTransactionType());
		transactionDTOResponse.setRemainingBalance(currentsTransaction.getRemainingBalance());
		transactionDTOResponse.setTransactionTime(currentsTransaction.getTransactionTime());
		transactionDTOResponse.setRecipient(currentsTransaction.getRecipient());
		transactionDTOResponse.setNarratives(currentsTransaction.getNarratives());
		transactionDTOResponse.setTransactionCategory(currentsTransaction.getTransactionCategory());
		return transactionDTOResponse;
	}

	@Override
	public List<TransactionDTOResponse> getMonthlyTransactionsByAccountNumber(Long accountNumber, String accountType,
			int year, int month) {
		if ("current".equalsIgnoreCase(accountType)) {
			// Retrieve the monthly currents transactions
			LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
			LocalDateTime endDate = startDate.withDayOfMonth(startDate.toLocalDate().lengthOfMonth()).withHour(23)
					.withMinute(59).withSecond(59);
			List<CurrentsTransactionEntity> monthlyCurrentsTransactions = currentsTransactionRepository
					.findByAccountnumberAndtransactionTimeBetweenOrderByTransactionTimeDesc(accountNumber, startDate,
							endDate);

			// Map the monthly currents transactions to transaction responses
			List<TransactionDTOResponse> transactionDTOResponses = new ArrayList<>();
			for (CurrentsTransactionEntity currentsTransaction : monthlyCurrentsTransactions) {
				TransactionDTOResponse transactionDTOResponse = mapFromCurrentsTransaction(currentsTransaction);
				transactionDTOResponses.add(transactionDTOResponse);
			}
			return transactionDTOResponses;
		} else {
			throw new RuntimeException("Invalid account type");
		}
	}

	@Override
	public List<TransactionDTOResponse> getQuarterlyTransactionsByAccountNumber(Long accountNumber, String accountType,
			int year, int quarter) {
		if ("current".equalsIgnoreCase(accountType)) {
			// Calculate the start and end months of the quarter
			int startMonth = (quarter - 1) * 3 + 1;
			int endMonth = startMonth + 2;
			if (endMonth > 12) {
				endMonth = 12;
			}

			// Retrieve the quarterly currents transactions
			List<TransactionDTOResponse> quarterlyTransactions = new ArrayList<>();
			for (int month = startMonth; month <= endMonth; month++) {
				YearMonth yearMonth = YearMonth.of(year, month);
				LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
				LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
				List<CurrentsTransactionEntity> monthlyCurrentsTransactions = currentsTransactionRepository
						.findByAccountnumberAndtransactionTimeBetweenOrderByTransactionTimeDesc(accountNumber,
								startDate, endDate);

				// Map the monthly currents transactions to transaction responses
				for (CurrentsTransactionEntity currentsTransaction : monthlyCurrentsTransactions) {
					TransactionDTOResponse transactionDTOResponse = mapFromCurrentsTransaction(currentsTransaction);
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
		if ("current".equalsIgnoreCase(accountType)) {
			// Retrieve the yearly currents transactions
			List<TransactionDTOResponse> yearlyTransactions = new ArrayList<>();
			for (int month = 1; month <= 12; month++) {
				YearMonth yearMonth = YearMonth.of(year, month);
				LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
				LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
				List<CurrentsTransactionEntity> monthlyCurrentsTransactions = currentsTransactionRepository
						.findByAccountnumberAndtransactionTimeBetweenOrderByTransactionTimeDesc(accountNumber,
								startDate, endDate);

				// Map the monthly currents transactions to transaction responses
				for (CurrentsTransactionEntity currentsTransaction : monthlyCurrentsTransactions) {
					TransactionDTOResponse transactionDTOResponse = mapFromCurrentsTransaction(currentsTransaction);
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

		if ("current".equalsIgnoreCase(accountType)) {
			
			
			List<Object[]> yearlyCurrentsTransactions = currentsTransactionRepository.
						findYearlySumExpenseByExpenseCategory(accountNumber,year);
			List<CurrentsTransactionEntity>  yearlyCurrentsTransaction = null;
			List<CurrentsTransactionEntity> yearlyTransactions = new ArrayList<>();

			
			Map<String, Object> transactionsByCatergory = new HashMap<>();
			
				for (Object[] row : yearlyCurrentsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);

				    LocalDateTime startDate = LocalDate.of(year, 1, 1).atStartOfDay();
			        LocalDateTime endDate = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
			        
			        		yearlyCurrentsTransaction = currentsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			                accountNumber, startDate, endDate, TransactionCategory.valueOf(transactionCategory));
						if(!yearlyCurrentsTransaction.isEmpty()) {
							yearlyTransactions.addAll(yearlyCurrentsTransaction);
							
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

		if ("current".equalsIgnoreCase(accountType)) {
			
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
			LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
			
			List<Object[]> monthlyCurrentsTransactions = currentsTransactionRepository.
						findMonthlySumExpenseByExpenseCategory(accountNumber,month,year);
			List<CurrentsTransactionEntity>  monthlyCurrentsTransaction = null;
			List<CurrentsTransactionEntity> monthlyTransactions = new ArrayList<>();

			
			Map<String, Object> transactionsByCatergory = new HashMap<>();
			
				for (Object[] row : monthlyCurrentsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
				    
				    monthlyCurrentsTransaction = currentsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			                accountNumber, startOfMonth, endOfMonth, TransactionCategory.valueOf(transactionCategory));
						if(!monthlyCurrentsTransactions.isEmpty()) {
							monthlyTransactions.addAll(monthlyCurrentsTransaction);
							
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

		
		if ("current".equalsIgnoreCase(accountType)) {
	        
			LocalDate currentDate = LocalDate.now();

	        LocalDate lastWeekStart = currentDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	        LocalDate lastWeekEnd = lastWeekStart.plusDays(6); // Last day of the week is 6 days after the start

	               
			List<Object[]> weeklyCurrentsTransactions = currentsTransactionRepository.
					findWeeklySumExpenseByExpenseCategory(accountNumber,lastWeekStart.atStartOfDay(),lastWeekEnd.atTime(23, 59, 59));
			List<CurrentsTransactionEntity>  weeklyCurrentsTransaction = null;
			List<CurrentsTransactionEntity> weeklyTransactions = new ArrayList<>();			
			
			Map<String, Object> transactionsByCatergory = new HashMap<>();
			
				for (Object[] row : weeklyCurrentsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
				    
						weeklyCurrentsTransaction = currentsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			                accountNumber, lastWeekStart.atStartOfDay(), lastWeekEnd.atTime(23, 59, 59), TransactionCategory.valueOf(transactionCategory));
						if(!weeklyCurrentsTransactions.isEmpty()) {
							weeklyTransactions.addAll(weeklyCurrentsTransaction);
							
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
			String accountType,DayTransaction date) {
		
		DayTransaction dayTransactionToday =  DayTransaction.today;
		DayTransaction dayTransactionYesterday =  DayTransaction.yesterday;
		
		if ("current".equalsIgnoreCase(accountType)) {
	        
			 LocalDate today = LocalDate.now();
		
			 LocalDate yesterday = today.minusDays(1);
		        
			 List<CurrentsTransactionEntity>  yesterdayOrTodayCurrentsTransaction = null;
			 List<CurrentsTransactionEntity> yesterdayOrTodayTransactions = new ArrayList<>();			
				
			 Map<String, Object> transactionsByCatergory = new HashMap<>();
				
			 
		if(dayTransactionYesterday.equals(date)) {
			
			List<Object[]> yesterdayOrTodayCurrentsTransactions = currentsTransactionRepository.
					findYesterdayOrTodaySumExpenseByExpenseCategory(accountNumber,LocalDateTime.of(yesterday, LocalTime.MIN),LocalDateTime.of(yesterday, LocalTime.MAX));
			
				for (Object[] row : yesterdayOrTodayCurrentsTransactions) {
				    Double totalBalance = (Double) row[0];
				    String transactionCategory = (String) row[1];
				    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
				    
				    yesterdayOrTodayCurrentsTransaction = currentsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			                accountNumber, LocalDateTime.of(yesterday, LocalTime.MIN), LocalDateTime.of(yesterday, LocalTime.MAX), TransactionCategory.valueOf(transactionCategory));
						if(!yesterdayOrTodayCurrentsTransactions.isEmpty()) {
							yesterdayOrTodayTransactions.addAll(yesterdayOrTodayCurrentsTransaction);
						}
				}
			 }
			 else if (dayTransactionToday.equals(date)) {
				 
				 List<Object[]> todaySavingsTransactions = currentsTransactionRepository.
							findYesterdayOrTodaySumExpenseByExpenseCategory(accountNumber,LocalDateTime.of(today, LocalTime.MIN),LocalDateTime.of(today, LocalTime.MAX));
					
						for (Object[] row : todaySavingsTransactions) {
						    Double totalBalance = (Double) row[0];
						    String transactionCategory = (String) row[1];
						    transactionsByCatergory.put("total_"+transactionCategory+"_expense", totalBalance);
						    
								yesterdayOrTodayCurrentsTransaction = currentsTransactionRepository.findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
					                accountNumber, LocalDateTime.of(today, LocalTime.MIN), LocalDateTime.of(today, LocalTime.MAX), TransactionCategory.valueOf(transactionCategory));
								if(!todaySavingsTransactions.isEmpty()) {
									yesterdayOrTodayTransactions.addAll(yesterdayOrTodayCurrentsTransaction);
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
		// TODO Auto-generated method stub
		if ("current".equalsIgnoreCase(accountType)) {
			List<CurrentsTransactionEntity>  rangedCurrentTransaction = currentsTransactionRepository
					.findByAccountnumberAndtransactionTimeBetweenOrderByTransactionTimeDesc(accountNumber, startDate, endDate);
			
			List<TransactionDTOResponse> transactionDTOResponses = new ArrayList<>();
			for (CurrentsTransactionEntity currentTransaction : rangedCurrentTransaction) {
				TransactionDTOResponse transactionDTOResponse = mapFromCurrentsTransaction(currentTransaction);
				transactionDTOResponses.add(transactionDTOResponse);
			}
			return transactionDTOResponses;
		}else {
			throw new RuntimeException("Invalid account type");
		}
	}
	


	
	
	
	
	
	
	
	


}
