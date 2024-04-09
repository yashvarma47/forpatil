package com.thinknxt.rba.services;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinknxt.rba.dto.Accounts;
import com.thinknxt.rba.dto.AddBenificiaryDTO;
import com.thinknxt.rba.dto.BudgetSetupDTO;
import com.thinknxt.rba.dto.TransactionRequestDTO;
import com.thinknxt.rba.dto.TransferRequestDTO;
import com.thinknxt.rba.entities.BenificiaryAccount;
import com.thinknxt.rba.response.BenificiaryResponse;
import com.thinknxt.rba.response.BudgetSetupResponse;
import com.thinknxt.rba.response.ExpenseCategoryResponse;
import com.thinknxt.rba.response.TransactionResponse;
import com.thinknxt.rba.utils.TransactionCategory;

import jakarta.validation.Valid;

public interface TransactionService {
	public TransactionResponse createTransaction(TransactionRequestDTO transactionRequestDTO, Accounts account);

	public TransactionResponse transferMoneyWithinAccounts(TransferRequestDTO transferRequestDTO,
			double senderTotalBalance, int senderCustomerId, String senderAccountType) throws JsonProcessingException;

	public List<Accounts> findAllAccount(List<Accounts> list);

	List<BenificiaryAccount> getBenificiaryByCustomerId(int	customerId);
	public BudgetSetupResponse getBudgetLimitByExpenseCategory(long accountNumber);
	public BudgetSetupResponse updateBudgetLimitByAccountNumber(long accountNumber,@Valid BudgetSetupDTO budgetSetupDTO);
	public BenificiaryResponse addBeneficiary(AddBenificiaryDTO addBenificiaryDTO);

	public ExpenseCategoryResponse getMonthlyExpensesByCategoryAndAccountNumberAndAccountType(Long accountNumber,
			String accountType, TransactionCategory expenseCategory);

	BudgetSetupResponse addBudgetLimitByAccountNumber(long accountNumber);
}
