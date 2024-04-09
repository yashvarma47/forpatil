package com.thinknxt.rba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.CurrentsTransactionEntity;
import com.thinknxt.rba.utils.TransactionCategory;

@Repository
@Generated
public interface CurrentsTransactionRepository extends JpaRepository<CurrentsTransactionEntity, Long> {

	@Query(value = "select ct.* from currents_transaction ct inner join transaction t on t.transaction_id = ct.transaction_id "
			+ "where "
			+ "t.transaction_status='SUCCESS' "
			+ "and ct.account_number = ?1 "
			+ "order By ct.transaction_time desc "
			+ "limit 10", nativeQuery = true)
	List<CurrentsTransactionEntity> findTop10ByAccountNumber(Long accountNumber);
	
	@Query(value = "select ct.* from currents_transaction ct inner join transaction t on t.transaction_id = ct.transaction_id "
				+ "where "
				+ "t.transaction_status='SUCCESS' "
				+ "and ct.account_number = ?1 "
				+ "AND ct.transaction_time BETWEEN ?2 AND ?3"
				, nativeQuery = true)
	List<CurrentsTransactionEntity> findByAccountnumberAndtransactionTimeBetweenOrderByTransactionTimeDesc(
			Long accountNumber, LocalDateTime startDate, LocalDateTime endDate);

	List<CurrentsTransactionEntity> findByAccountNumberAndTransactionTimeBetweenAndTransactionCategoryOrderByTransactionTimeDesc(
			Long accountNumber, LocalDateTime startDate, LocalDateTime endDate,
			TransactionCategory transactionCategory);

	@Query(value = "SELECT SUM(transaction_amount) AS sumOfExpenseByExpenseCategory, "
			+ "transaction_category AS transactionCategory " + "FROM currents_transaction "
			+ "WHERE account_number = ?1 AND YEAR(transaction_time) = ?2 AND transaction_type = 'Debit' AND transaction_category IS NOT NULL "
			+ "GROUP BY transaction_category", nativeQuery = true)
	List<Object[]> findYearlySumExpenseByExpenseCategory(Long accountNumber, int year);

	@Query(value = "SELECT SUM(transaction_amount) AS sumOfExpenseByExpenseCategory, "
			+ "transaction_category AS transactionCategory " + "FROM currents_transaction "
			+ "WHERE account_number = ?1 AND MONTH(transaction_time) = ?2 AND YEAR(transaction_time) = ?3 AND transaction_type = 'Debit' AND transaction_category IS NOT NULL "
			+ "GROUP BY transaction_category", nativeQuery = true)
	List<Object[]> findMonthlySumExpenseByExpenseCategory(Long accountNumber, int month, int year);

	@Query(value = "SELECT SUM(transaction_amount) AS sumOfExpenseByExpenseCategory, "
			+ "transaction_category AS transactionCategory " + "FROM currents_transaction "
			+ "WHERE account_number = ?1 " + "AND transaction_type = 'Debit' "
			+ "AND transaction_time BETWEEN ?2 AND ?3 AND transaction_category IS NOT NULL " + "GROUP BY transaction_category", nativeQuery = true)
	List<Object[]> findWeeklySumExpenseByExpenseCategory(Long accountNumber, LocalDateTime startDate,
			LocalDateTime endDate);

	@Query(value = "SELECT SUM(transaction_amount) AS sumOfExpenseByExpenseCategory, "
			+ "transaction_category AS transactionCategory " + "FROM currents_transaction "
			+ "WHERE account_number = ?1 " + "AND transaction_type = 'Debit' "
			+ "AND transaction_time BETWEEN ?2 AND ?3 AND transaction_category IS NOT NULL " + "GROUP BY transaction_category", nativeQuery = true)
	List<Object[]> findYesterdayOrTodaySumExpenseByExpenseCategory(Long accountNumber,LocalDateTime yesterdayStartDate,LocalDateTime yesterdayEndDate);

}
