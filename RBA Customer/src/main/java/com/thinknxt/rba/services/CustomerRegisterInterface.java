package com.thinknxt.rba.services;

import java.sql.Date;
import java.util.List;

import com.thinknxt.rba.dto.CustomerDTO;
import com.thinknxt.rba.dto.LoginDTO;
import com.thinknxt.rba.entities.Customer;
import com.thinknxt.rba.response.CustomerResponse;

public interface CustomerRegisterInterface {

	public CustomerResponse registerCustomer(CustomerDTO customerDTO);
	public LoginDTO createLoginDTO(int userId, String password);
	public CustomerResponse validateUserToRetrieveUserId(String pan, String aadhar, Date dateOfBirth, String phoneNumber);
	public List<Customer> getAllCustomers();
	public void updateNotifications(Integer customerId, Long accountNumber);
	public CustomerResponse resetPassword(String token, String newPassword);
	public CustomerResponse validateUserToRetrievePassword(int userId, String pan, Date dateOfBirth,
			String phoneNumber);
	public CustomerResponse validateToken(String token);
}
