package com.thinknxt.rba.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.thinknxt.rba.config.PasswordResetTokenService;
import com.thinknxt.rba.dto.AddressDTO;
import com.thinknxt.rba.dto.CustomerDTO;
import com.thinknxt.rba.dto.EmailRequest;
import com.thinknxt.rba.dto.LoginDTO;
import com.thinknxt.rba.entities.Address;
import com.thinknxt.rba.entities.Customer;
import com.thinknxt.rba.entities.Notifications;
import com.thinknxt.rba.entities.PasswordResetToken;
import com.thinknxt.rba.repository.AddressRepository;
import com.thinknxt.rba.repository.CustomerRepository;
import com.thinknxt.rba.repository.NotificationsRepository;
import com.thinknxt.rba.repository.PasswordResetTokenRepository;
import com.thinknxt.rba.response.CustomerResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerRegisterServiceImpl implements CustomerRegisterInterface {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private PasswordResetTokenService tokenService;
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	// Define setter method for CustomerRepository
	public void setCustomerRepository(CustomerRepository customerRepository,
			PasswordResetTokenRepository passwordResetTokenRepository) {
		this.customerRepository = customerRepository;
		this.passwordResetTokenRepository = passwordResetTokenRepository;

	}

	/**
	 * Registering new customer using CustomerDTO by an authorized Admin
	 * 
	 * @param customerDTO The data transfer object containing customer information.
	 * @return CustomerResponse indicating the result of the new Customer
	 *         registration operation.
	 */
	@Override
	public CustomerResponse registerCustomer(CustomerDTO customerDTO) {
		log.info("Inside registerCustomer function of  CustomerRegisterServiceImpl");

		// Check if the customer already exists by PAN or AADHAR number
		if (customerRepository.existsByPanNumber(customerDTO.getPanNumber())
				|| customerRepository.existsByAadharNumber(customerDTO.getAadharNumber())) {
			log.info("Customer Exist as condition Inside registerCustomer function of  CustomerRegisterServiceImpl");
			return new CustomerResponse("This customer already exists!!!!", 404, null);
		} else {
			log.info(
					"Customer doesn't Exist as condition Inside registerCustomer function of  CustomerRegisterServiceImpl");
			// Generate unique customerID
			int uuid = UniqueStringGenerator.generateUniqueNumericID();

			// Check if the uuid is unique i.e it is not present in the database.
			while (customerRepository.existsByCustomerId(uuid)) {
				uuid = UniqueStringGenerator.generateUniqueNumericID();
			}

			String autoGeneratePassword = UniqueStringGenerator.generatePassword(8);

			String subject = "New Customer Registration - Welcome!";

			String body = String.format(
					"Dear %s,\n\n" + "Thank you for registering with our service! Your customer ID is: %s\n\n"
							+ "Your system generated default password is : %s\n\n" + "Best regards,\n"
							+ "Retail Banking Team",
					customerDTO.getFirstName() + " " + customerDTO.getLastName(), uuid, autoGeneratePassword);
			;

			String url = "http://localhost:1014/email/sendEmail";

			String mailResponse = "";

			// Save customer details in customers table
			Customer customer = createCustomerEntity(customerDTO, uuid);
			Customer customerEntityResponse = customerRepository.save(customer);

			// Save address details in address table
			Address address = createAddressEntity(customerDTO.getAddressDTO(), uuid);
			Address addressEntityResponse = addressRepository.save(address);

			if (customerEntityResponse != null && addressEntityResponse != null) {
				EmailRequest request = new EmailRequest(customerDTO.getEmail(), subject, body);
				ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
				if (response.getStatusCode().is2xxSuccessful()) {
					mailResponse = "Email has been sent successfully to mail Id :" + customerDTO.getEmail();
				} else {
					mailResponse = "Failed to send email.";
				}

				// Make a request to the Login Service for user registration using restTemplate
				String loginServiceEndpoint = "http://localhost:1010/api/retailbanking/login/register-user";
				restTemplate.postForObject(loginServiceEndpoint,
						createLoginDTO(uuid, (passwordEncoder.encode(autoGeneratePassword))), String.class);
				log.info("End of  registerCustomer function of  CustomerRegisterServiceImpl");
			}
			return new CustomerResponse("Customer: " + uuid + " has been registered successfully " + mailResponse, 200,
					null);
		}
	}

	/**
	 * Create Address entity based on AddressDTO and customerId.
	 * 
	 * @param addressDTO The data transfer object containing address information.
	 * @param customerId The unique identifier for the customer.
	 * @return Address entity.
	 */
	public Address createAddressEntity(AddressDTO addressDTO, int customerId) {
		log.info("Inside createAddressEntity function of  CustomerRegisterServiceImpl");
		Address address = new Address();
		address.setStreet(addressDTO.getStreet());
		address.setCity(addressDTO.getCity());
		address.setState(addressDTO.getState());
		address.setZipcode(addressDTO.getZipcode());
		address.setCustomerId(customerId);
		log.info("End of createAddressEntity function of  CustomerRegisterServiceImpl");
		return address;
	}

	/**
	 * Create Customer entity based on CustomerDTO and customerId.
	 * 
	 * @param customerDTO The data transfer object containing customer information.
	 * @param customerId  The unique identifier for the customer.
	 * @return Customer entity.
	 */
	public Customer createCustomerEntity(CustomerDTO customerDTO, int customerId) {
		log.info("Inside createCustomerEntity function of  CustomerRegisterServiceImpl");
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setDateOfBirth(customerDTO.getDateOfBirth());
		customer.setAadharNumber(customerDTO.getAadharNumber());
		customer.setPanNumber(customerDTO.getPanNumber());
		customer.setEmail(customerDTO.getEmail());
		customer.setFirstName(customerDTO.getFirstName());
		customer.setGender(customerDTO.getGender());
		customer.setLastName(customerDTO.getLastName());
		customer.setPhoneNumber(customerDTO.getPhoneNumber());
		log.info("End of createCustomerEntity function of  CustomerRegisterServiceImpl");
		return customer;
	}

	/**
	 * Create LoginDTO to save the userId and password in the Login_Details Table.
	 * 
	 * @param userId   The unique identifier for the user.
	 * @param password The password for the user.
	 * @return LoginDTO contains userId and password.
	 */
	public LoginDTO createLoginDTO(int userId, String password) {
		log.info("Inside createLoginDTO function of  CustomerRegisterServiceImpl");
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setUserId(userId);
		loginDTO.setPassword(password);
		log.info("End of createLoginDTO function of  CustomerRegisterServiceImpl");
		return loginDTO;
	}

	/**
	 * Service to fetch the existing customer details using CustomerId, Email And
	 * PhoneNumber.
	 * 
	 * @param customerId  The unique identifier for the customer.
	 * @param email       The email address of the customer.
	 * @param phoneNumber The phone number of the customer.
	 * @return CustomerResponse containing customer information.
	 */
	public CustomerResponse getCustomerOnIdEmailAndPhoneNumber(int customerId, String email, String phoneNumber) {
		log.info("Inside getCustomerOnIdEmailAndPhoneNumber function of  CustomerRegisterServiceImpl");
		Customer customerDetail = customerRepository.findByCustomerIdAndEmailAndPhoneNumber(customerId, email,
				phoneNumber);
		CustomerResponse response;
		if (customerDetail != null) {
			log.info(
					"Customer found Inside getCustomerOnIdEmailAndPhoneNumber function of  CustomerRegisterServiceImpl");
			response = new CustomerResponse("Customer has been found!!", 200, customerDetail);
		} else {
			log.info(
					"Customer not  found Inside getCustomerOnIdEmailAndPhoneNumber function of  CustomerRegisterServiceImpl");
			response = new CustomerResponse("Customer does not exist!!", 400, null);
		}
		log.info("End of getCustomerOnIdEmailAndPhoneNumber function of  CustomerRegisterServiceImpl");
		return response;
	}

	/**
	 * new UI implementation Service method to retrieve customer details based on
	 * the provided customer ID.
	 *
	 * @param customerId The unique identifier of the customer.
	 * @return CustomerResponse containing information about the result of the
	 *         operation.
	 */
	@Transactional(readOnly = true)
	public CustomerResponse getCustomerById(int customerId) {
		log.info("Inside getCustomerById function of CustomerService");
		Customer customerDetail = customerRepository.findByCustomerId(customerId);
		CustomerResponse response;
		if (customerDetail != null) {
			log.info("Customer found Inside getCustomerById function of CustomerService");
			response = new CustomerResponse("Customer has been found!!", 200, customerDetail);
		} else {
			log.info("Customer not found Inside getCustomerById function of CustomerService");
			response = new CustomerResponse("Customer does not exist!!", 404, null);
		}
		log.info("End of getCustomerById function of CustomerService");
		return response;
	}

	/**
	 * Service to validate user information to retrieve userId.
	 * 
	 * @param pan         The PAN number of the user.
	 * @param aadhar      The Aadhar number of the user.
	 * @param dateOfBirth The date of birth of the user.
	 * @param phoneNumber The phone number of the user.
	 * @return CustomerResponse indicating the result of the validation operation.
	 */
	@Override
	public CustomerResponse validateUserToRetrieveUserId(String pan, String aadhar, Date dateOfBirth,
			String phoneNumber) {
		log.info("Inside validateUser function of  ForgotServiceImpl class");
		Customer customer = customerRepository.findByPanNumberAndAadharNumberAndDateOfBirthAndPhoneNumber(pan, aadhar,
				dateOfBirth, phoneNumber);
		if (customer != null) {
			log.info("Customer found Inside validateUser function of  ForgotServiceImpl class");

			String subject = "Retrived Customer Id";

			String body = String.format(
					"Dear %s,\n\n" + "Your customer ID is: %s\n\n" + "Best regards,\n" + "Retail Banking Team",
					customer.getFirstName() + " " + customer.getLastName(), customer.getCustomerId());
			;

			String url = "http://localhost:1014/email/sendEmail";

			String mailResponse = "";
			EmailRequest request = new EmailRequest(customer.getEmail(), subject, body);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				mailResponse = "Email has been sent successfully to mail Id :" + customer.getEmail();
			} else {
				mailResponse = "Failed to send email.";
			}
			log.info("Email has been sent successfully to mail Id :" + customer.getEmail());
			log.info("End of CustomerId retrival Function of CustomerRegisterServiceImpl");

			return new CustomerResponse("Customer found", 200, null);
		} else {
			log.info("Customer not found Inside validateUser function of  ForgotServiceImpl class");
			return new CustomerResponse("Invalid data or user not found", 404, null);
		}

	}

	/**
	 * Retrieve all customers.
	 *
	 * @return List of all customers.
	 */
	@Override
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

//    // siddharth 

	@Transactional
	public ResponseEntity<CustomerResponse> updateCustomerDetails(int customerId, CustomerDTO updatedCustomer) {
		// Check if the customer exists
		if (!customerRepository.existsById(customerId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new CustomerResponse("Customer not found", HttpStatus.NOT_FOUND.value(), null));
		}

		// Update the customer details
		Customer existingCustomer = customerRepository.findById(customerId).orElse(null);
		if (existingCustomer != null) {
			// Update the fields based on the CustomerDTO
			existingCustomer.setFirstName(updatedCustomer.getFirstName());
			existingCustomer.setLastName(updatedCustomer.getLastName());
			existingCustomer.setDateOfBirth(updatedCustomer.getDateOfBirth());
			existingCustomer.setGender(updatedCustomer.getGender());
			existingCustomer.setEmail(updatedCustomer.getEmail());
			existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
			existingCustomer.setPanNumber(updatedCustomer.getPanNumber());
			existingCustomer.setAadharNumber(updatedCustomer.getAadharNumber());

			// Get the existing address associated with the customer
			Address existingAddress = existingCustomer.getAddress();
			log.info("Existing Address {}", existingAddress);

			// Update address details directly within Address entity
			existingAddress.setStreet(updatedCustomer.getAddressDTO().getStreet());
			existingAddress.setCity(updatedCustomer.getAddressDTO().getCity());
			existingAddress.setState(updatedCustomer.getAddressDTO().getState());
			existingAddress.setZipcode(updatedCustomer.getAddressDTO().getZipcode());

			// Save the updated customer (address details will be updated automatically)
			customerRepository.save(existingCustomer);

			// Return the response with the updated customer
			return ResponseEntity
					.ok(new CustomerResponse("Customer updated successfully", HttpStatus.OK.value(), existingCustomer));
		} else {
			// Handle the case where the existing customer is null (unexpected scenario)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new CustomerResponse("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
		}
	}

	public CustomerResponse getCustomerOnId(int customerId) {
		log.info("Inside getCustomerOnId function of CustomerRegisterServiceImpl");

		// Call the repository method to find customer by customerId
		Customer customerDetail = customerRepository.findById(customerId).orElse(null);
		CustomerResponse response;

		if (customerDetail != null) {
			log.info("Customer found inside getCustomerOnId function of CustomerRegisterServiceImpl");
			response = new CustomerResponse("Customer has been found!!", 200, customerDetail);
		} else {
			log.info("Customer not found inside getCustomerOnId function of CustomerRegisterServiceImpl");
			response = new CustomerResponse("Customer does not exist!!", 400, null);
		}

		log.info("End of getCustomerOnId function of CustomerRegisterServiceImpl");
		return response;
	}

	@Override
	@Transactional
	public void updateNotifications(Integer customerId, Long accountNumber) {
		// Accounts accountsEntity =
		// accountRepository.findByAccountnumber(accountNumber);
		Notifications notifications = new Notifications();
//		List<Notifications> notification=notificationsRepository.findAllByCustomerId(customerId);
//		if (notification != null) {
		notifications.setCustomerId(customerId);
		notifications.setAccountNumber(accountNumber);
		notifications.setAccountBalanceUpdates("NA");
		notifications.setAccountBlockAlerts("NA");
		notifications.setLowBalanceAlerts("NA");
		notifications.setTxnConfirmation("NA");
		notifications.setSecurityAlerts("NA");
		notificationsRepository.save(notifications);
//		}
	}

	/**
	 * @author nirajku Service to validate user information to get reset password
	 *         link.
	 * 
	 * @param userId      The UserId of the user.
	 * @param pan         The PAN number of the user.
	 * @param dateOfBirth The date of birth of the user.
	 * @param phoneNumber The phone number of the user.
	 * @return CustomerResponse indicating the result of the validation operation.
	 */
	@Override
	public CustomerResponse validateUserToRetrievePassword(int userId, String pan, Date dateOfBirth,
			String phoneNumber) {
		log.info("Inside validate User function for sending password link of CustomerRegister ServiceImpl class");

		Customer customer = customerRepository.findByCustomerIdAndPanNumberAndDateOfBirthAndPhoneNumber(userId, pan,
				dateOfBirth, phoneNumber);
		if (customer != null) {
//			Creating token for the customer
			log.info("Generating the token for the customer in  ForgotServiceImpl class for Password reset link");
			String token = tokenService.generateTokenForUser(userId);

			String resetPasswordLink = "http://localhost:4200/forgot-password/reset-password?token=" + token;

			log.info("Customer found Inside validateUser function of  ForgotServiceImpl class for Password retrival");

			String subject = "Link to reset password ";

			String body = String.format(
					"Dear %s,\n\n" + "Your reset Password Link is: %s\n\n" + "This link is Valid for 1 hour \n\n\n"
							+ "Best regards,\n" + "Retail Banking Team",
					customer.getFirstName() + " " + customer.getLastName(), resetPasswordLink);

			String url = "http://localhost:1014/email/sendEmail";

			String mailResponse = "";
			EmailRequest request = new EmailRequest(customer.getEmail(), subject, body);
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//			if (response.getStatusCode().is2xxSuccessful()) {
//				mailResponse = "Email has been sent successfully to mail Id :" + customer.getEmail();
//			} else {
//				mailResponse = "Failed to send email.";
//			}
			log.info("Email has been sent successfully to mail Id :" + customer.getEmail());
			log.info("End of ForgotPass retrival Function of CustomerRegisterServiceImpl");

			return new CustomerResponse("Email sent successfully!!", 200, null);
		} else {
			log.info("Customer not found Inside validateUser function of  ForgotServiceImpl class");
			return new CustomerResponse("Invalid data or user not found", 404, null);
		}
	}

//	This method will be validating the token so that it can display the Front end

	/**
	 * @author nirajku Service to validate the token from the reset password link.
	 * 
	 * @param Token generated token for password reset
	 * @return CustomerResponse indicating the result of the validation operation.
	 */
	@Override
	public CustomerResponse validateToken(String token) {
		log.info("Inside validateToken to validate the passwordResetToken of CustomerRegister ServiceImpl class");
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

		if (passwordResetToken != null) {
			log.info("Customer found with given token in the table of Password reset token");
			if (isTokenExpired(passwordResetToken.getExpiryTime())) {
				// Token is expired
				log.info("Given token for the customer is expired");

				return new CustomerResponse("Token is expired", 401, null);
			} else {
				// Token is valid
				log.info("Given token is valid and the response is returned as token valid");

				return new CustomerResponse("Token is valid", 200, null);
			}
		} else {
			// Token is invalid
			log.info("Given token is not valid or not found in table ");

			return new CustomerResponse("Token is invalid", 404, null);
		}
	}

	private boolean isTokenExpired(Timestamp timestamp) {
		log.info("Token is being checked weather is expired or not");
		long currentTimeMillis = System.currentTimeMillis();
		long expiryTimeMillis = timestamp.getTime();

		// Token is expired if the current time is after the expiry time
		log.info("Resturning teh result of the token weather is expired or not");

		return currentTimeMillis > expiryTimeMillis;
	}

//		 Reseting password using rest Template call
	/**
	 * @author nirajku Service to to reset password by taking the new password from
	 *         UI.
	 * 
	 *         calling the rest template from login to reset password
	 * 
	 * @param Token       generated token for password reset
	 * @param newPassword New password entered from UI
	 * @return CustomerResponse indicating the result of the validation operation.
	 */

	@Override
	public CustomerResponse resetPassword(String token, String newPassword) {
		log.info(
				"Inside the resetPassword function to reset the password with the given token and newPassword for the customer");
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

		if (passwordResetToken != null) {
			log.info(
					"Inside the resetPassword function to reset the password with the given token and Customer found with the token");
			if (isTokenExpired(passwordResetToken.getExpiryTime())) {
				log.info(
						"Inside the resetPassword function to reset the password with the given token and Customer found with the token but token is expired");
				// Token is expired
				return new CustomerResponse("Token is expired", 401, null);
			} else {
				log.info(
						"Inside the resetPassword function to reset the password with the given token and Customer found with the token and token is valid");

				Customer customer = customerRepository.findByCustomerId(passwordResetToken.getUserId());

				log.info(
						"Inside the resetPassword function to reset the password and send user id and new password via rest template");

				String resturl = "http://localhost:1010/api/retailbanking/login/reset-password/"
						+ passwordResetToken.getUserId() + "?newPassword=" + newPassword;
				// calling rest template to reset the password
				int responseCode = restTemplate.getForObject(resturl, Integer.class);
				log.info("Inside the resetPassword function and got the response of the rest API call");
// Taking the response code and according to that if and switch case is being worked
				if (responseCode != 0) {
					log.info(
							"Inside the resetPassword function and got the response of the rest API call and it was not null");

					switch (responseCode) {
					case 200:
						// Password reset successful
						log.info("Inside the resetPassword function and password has been reseted successfully");
						String subject = "Password Reset Alert";

						String body = String.format("Hey %s,\n\n"
								+ "Your password has been reset. If this wasn't you, feel free to reach out to our customer care team.\n\n"
								+ "Best regards,\n" + "The Retail Banking Team",
								customer.getFirstName() + " " + customer.getLastName());
						String url = "http://localhost:1014/email/sendEmail";

						EmailRequest request = new EmailRequest(customer.getEmail(), subject, body);
						restTemplate.postForEntity(url, request, String.class);
						log.info(
								"Inside the resetPassword function and password has been reseted successfully and sent the mail for the same");
//						tokenService.removeToken(passwordResetToken.getUserId());
						passwordResetTokenRepository.deleteById(passwordResetToken.getUserId());

						return new CustomerResponse("Password reset successfully", 200, null);

					case 400:
						log.info(
								"Inside the resetPassword function and response is New password cannot be the same as the old password");
						// New password cannot be the same as the old password
						return new CustomerResponse("New password cannot be the same as the old password", 400, null);
					default:
						log.info("Inside the resetPassword function and response is Customer ID not found");
						// Customer ID not found
						return new CustomerResponse("Customer ID not found", 404, null);

					}
				} else {
					log.info("Inside the resetPassword function and response is Password not reset");
					// Password reset failed
					return new CustomerResponse("Password not reset", 422, null);
				}
			}
		} else {
			log.info("Inside the resetPassword function and response is Token is not present");
			// Token is not present
			return new CustomerResponse("Token is not present", 404, null);
		}

	}

}
