package com.thinknxt.rba.controllers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thinknxt.rba.dto.AddressDTO;
import com.thinknxt.rba.dto.CustomerDTO;
import com.thinknxt.rba.dto.ForgotIdDTO;
import com.thinknxt.rba.dto.ForgotPasswordDTO;
import com.thinknxt.rba.entities.Address;
import com.thinknxt.rba.entities.Customer;
import com.thinknxt.rba.entities.Notifications;
import com.thinknxt.rba.repository.AddressRepository;
import com.thinknxt.rba.repository.CustomerRepository;
import com.thinknxt.rba.repository.NotificationsRepository;
import com.thinknxt.rba.response.AllCustomerResponse;
import com.thinknxt.rba.response.CustomerResponse;
import com.thinknxt.rba.response.NotificationResponse;
import com.thinknxt.rba.services.CustomerRegisterServiceImpl;
import com.thinknxt.rba.utils.NotificationStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequestMapping("/customer")
@Slf4j
public class CustomerController {

	/**
	 * Customer Controller contains API's related to: 1) Register new customer 2)
	 * Fetching the existing customer details based on customerId, email, and
	 * phoneNumber 3) Retrieve userId of customer
	 */
	@Autowired
	private CustomerRegisterServiceImpl customerRegisterServiceImpl;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AddressRepository addressRepository;
	
	public CustomerController(CustomerRegisterServiceImpl customerRegisterServiceImpl) {
//		this.restTemplate = restTemplate;
		this.customerRegisterServiceImpl = customerRegisterServiceImpl;
	}

	/**
	 * Endpoint to validate user information and retrieve userId.
	 * 
	 * @param forgotIdDTO The data transfer object containing user information.
	 * @return ResponseEntity with CustomerResponse indicating the result of the
	 *         operation.
	 */
	@Operation(summary = "Forgot UserID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Customer found"),
			@ApiResponse(responseCode = "404", description = "Invalid data or user not found") })
	@PostMapping("/forgot-userid")
	public ResponseEntity<CustomerResponse> validateUserToRetrieveUserId(@RequestBody @Valid ForgotIdDTO forgotIdDTO) {
		log.info("Inside validateUser function of class ForgotController  ");
		return ResponseEntity.ok(customerRegisterServiceImpl.validateUserToRetrieveUserId(forgotIdDTO.getPan(),
				forgotIdDTO.getAadhar(), forgotIdDTO.getDateOfBirth(), forgotIdDTO.getPhoneNumber()));
	}

//	@PostMapping("/forgot-password")
//	public ResponseEntity<CustomerResponse> validateUserPassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
//		
//		 log.info("Inside validateUserPassword function of class ForgotController  ");
//		 
//		return ResponseEntity.ok(forgotServiceImpl.validateUserPassword(forgotPasswordDTO.getUserId(), forgotPasswordDTO.getPan(),
//				forgotPasswordDTO.getDateOfBirth(), forgotPasswordDTO.getPhoneNumber()));
//	}

	/**
	 * Endpoint to register a new customer.
	 * 
	 * @param customerDTO The data transfer object containing customer information.
	 * @return ResponseEntity with CustomerResponse indicating the result of the
	 *         operation.
	 */
	@Operation(summary = "Register Customer")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Customer registered successfully"),
			@ApiResponse(responseCode = "200", description = "Customer registration failed") })
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/customer/register")
	public ResponseEntity<CustomerResponse> registerCustomer(@RequestBody @Valid CustomerDTO customerDTO) {
		log.info(" inside registerCustomer function of class CustomerController");
		CustomerResponse customerResponse = customerRegisterServiceImpl.registerCustomer(customerDTO);
		if (customerResponse.getStatus() == 201) {
			log.info("registering customer in class CustomerController");
			return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
		} else {
			log.info("Failed to register customer in class CustomerController");
			return new ResponseEntity<>(customerResponse, HttpStatus.OK);
		}
	}

	/**
	 * Endpoint to fetch customer details based on customerId, email, and
	 * phoneNumber.
	 * 
	 * @param customerId  The unique identifier for the customer.
	 * @param email       The email address of the customer.
	 * @param phoneNumber The phone number of the customer.
	 * @return ResponseEntity with CustomerResponse containing customer information.
	 */
	@Operation(summary = "Get Customer Information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Customer information retrieved successfully") })
	@GetMapping("/customer/{customerId}/{email}/{phoneNumber}")
	public ResponseEntity<CustomerResponse> getCustomerInfo(@PathVariable int customerId, @PathVariable String email,
			@PathVariable String phoneNumber) {
		log.info("Received request to get customer information in class CustomerController");
		CustomerResponse customerResponse = customerRegisterServiceImpl.getCustomerOnIdEmailAndPhoneNumber(customerId,
				email, phoneNumber);
		log.info("Returning customer information in class CustomerController");
		return new ResponseEntity<>(customerResponse, HttpStatus.OK);
	}

	/**
	 * Endpoint to fetch customer details based on customerId
	 * 
	 * @param customerId The unique identifier for the customer.
	 * @return ResponseEntity with CustomerResponse containing customer information.
	 */
	@Operation(summary = "Get Customer Information by ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Customer information retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Customer not found") })
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<CustomerResponse> getCustomerInfoById(@PathVariable int customerId) {
		log.info("Received request to get customer information by ID in class CustomerController");
		CustomerResponse customerResponse = customerRegisterServiceImpl.getCustomerById(customerId);
		log.info("Returning customer information by ID in class CustomerController");
		return new ResponseEntity<>(customerResponse, HttpStatus.OK);
	}

	/**
	 * new UI Implementation Controller method to fetch customer details based on
	 * the provided customer ID.
	 *
	 * @param customerId The positive integer representing the unique identifier of
	 *                   the customer.
	 * @return ResponseEntity<CustomerResponse> containing the result of the
	 *         customer details retrieval.
	 */
	@GetMapping("/fetchCustomer/{customerId}")
	public ResponseEntity<CustomerResponse> getCustomerDetails(
			@PathVariable @Positive(message = "Customer ID must be a positive integer") int customerId) {
		CustomerResponse customer = customerRegisterServiceImpl.getCustomerById(customerId);
		try {
			log.info("Inside getCustomerDetails method");
			if (customer == null) {
				log.info("Customer details not found for ID: {}", customerId);
				return new ResponseEntity<>(customer, HttpStatus.BAD_REQUEST);
			}
			log.info("Customer details found for ID: {}", customerId);

			return new ResponseEntity<>(customer, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("An error occurred in getCustomerDetails method", ex);
			return new ResponseEntity<>(customer, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Siddharth
	@GetMapping("/customers/{customerId}")
	public ResponseEntity<CustomerResponse> getCustomerDetailsforSetUpProfile(@PathVariable int customerId) {
		log.info("Received request to get customer details with ID: {}", customerId);

		// Call the service to retrieve customer information based on customerId
		CustomerResponse customerResponse = customerRegisterServiceImpl.getCustomerOnId(customerId);

		log.info("Response after getting customer details: {}", customerResponse);
		return new ResponseEntity<>(customerResponse, HttpStatus.OK);
	}

	/**
	 * Handles PUT requests to update customer details.
	 *
	 * This method receives a request to update customer information identified by
	 * the provided customer ID. It validates the updated customer data and
	 * delegates the update operation to the service layer. The response includes
	 * details about the success or failure of the update operation.
	 *
	 * @param customerId      The unique identifier of the customer to be updated.
	 * @param updatedCustomer The updated customer data provided in the request
	 *                        body.
	 * @return ResponseEntity<CustomerResponse> containing the result of the update
	 *         operation.
	 */
	@PutMapping("/customer/update/{customerId}")
	public ResponseEntity<CustomerResponse> updateCustomerDetails(@PathVariable int customerId,
			@RequestBody @Validated CustomerDTO updatedCustomer) {
		log.info("Received request to update customer with ID: {}", customerId);
		ResponseEntity<CustomerResponse> customerResponse = customerRegisterServiceImpl
				.updateCustomerDetails(customerId, updatedCustomer);
		log.info("Response After Updating Customer Details: {}", customerResponse);
		return customerResponse;
	}

	/**
	 * @author vipulp Retrieve all customers.
	 *
	 * @return ResponseEntity with the list of all customers.
	 */
	@Operation(summary = "Get All Customers")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Customers retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "No customers found") })
	@GetMapping("/allCustomer")
	public ResponseEntity<AllCustomerResponse<List<Customer>>> getAllCustomers() {
		try {
			List<Customer> customers = customerRegisterServiceImpl.getAllCustomers();
			return ResponseEntity.ok(new AllCustomerResponse<>("Customers retrieved successfully", 200, customers));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(new AllCustomerResponse<>("No customers found", 404, null));
		}
	}

	@GetMapping("/updateNotification/{notificationVia}/{notificationType}/{customerId}")
	public ResponseEntity<NotificationResponse> updateNotifications(@PathVariable String notificationVia,
			@PathVariable String notificationType, @PathVariable int customerId) {

		NotificationResponse notificationResponse = new NotificationResponse();

		if (StringUtils.equalsIgnoreCase(notificationVia, NotificationStatus.Email.toString())) {
			if (StringUtils.equalsIgnoreCase(notificationType, NotificationStatus.Txn_Confirmation.toString())) {
				notificationsRepository.updateAllByCustomerIdforTxnConfirmation(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType, NotificationStatus.Security_alerts.toString())) {
				notificationsRepository.updateAllByCustomerIdforSecurityAlerts(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType,
					NotificationStatus.Low_balance_alerts.toString())) {
				notificationsRepository.updateAllByCustomerIdforLowBalanceAlerts(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType,
					NotificationStatus.Account_block_alerts.toString())) {
				notificationsRepository.updateAllByCustomerIdforAccountBlockAlerts(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType,
					NotificationStatus.Account_balance_updates.toString())) {
				notificationsRepository.updateAllByCustomerIdforAccountBalanceUpdates(notificationVia, customerId);
			}
			notificationResponse.setMessage("Email will be sent to your registered emailID");
			notificationResponse.setStatus(200);

		} else if (StringUtils.equalsIgnoreCase(notificationVia, NotificationStatus.NA.toString())) {
			if (StringUtils.equalsIgnoreCase(notificationType, NotificationStatus.Txn_Confirmation.toString())) {
				notificationsRepository.updateAllByCustomerIdforTxnConfirmationNA(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType, NotificationStatus.Security_alerts.toString())) {
				notificationsRepository.updateAllByCustomerIdforSecurityAlertsNA(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType,
					NotificationStatus.Low_balance_alerts.toString())) {
				notificationsRepository.updateAllByCustomerIdforLowBalanceAlertsNA(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType,
					NotificationStatus.Account_block_alerts.toString())) {
				notificationsRepository.updateAllByCustomerIdforAccountBlockAlertsNA(notificationVia, customerId);
			} else if (StringUtils.equalsIgnoreCase(notificationType,
					NotificationStatus.Account_balance_updates.toString())) {
				notificationsRepository.updateAllByCustomerIdforAccountBalanceUpdatesNA(notificationVia, customerId);
			}
			notificationResponse.setMessage("You will stop recieving email from now onwards.");
			notificationResponse.setStatus(302);

		} else if (StringUtils.equalsIgnoreCase(notificationVia, NotificationStatus.SMS.toString())) {
			// under progress

		}
		return new ResponseEntity<>(notificationResponse, HttpStatus.OK);

	}

	@SuppressWarnings("unlikely-arg-type")
	@GetMapping("/getNotificationData/{customerID}/{accountNumber}")
	public ResponseEntity<NotificationResponse> getNotificationData(@PathVariable Integer customerID,
			@PathVariable Long accountNumber) {
		NotificationResponse notificationResponse = new NotificationResponse();
		Notifications notification = new Notifications();
		List<Notifications> notificationList = null;
		if (accountNumber != 0) {
			notification = notificationsRepository.findByAccountNumber(accountNumber);
			if (notification != null) {
				notificationResponse.setStatus(200);
				notificationResponse.setMessage("got data for account number " + accountNumber);
				notificationResponse.setData(notification);
			} else {
				notificationResponse.setStatus(400);
				notificationResponse.setMessage("Account not found");
				notificationResponse.setData(null);
			}
		} else if (!NumberUtils.INTEGER_ZERO.equals(customerID)) {
			notificationList = notificationsRepository.findAllByCustomerId(customerID);
			if (notificationList != null) {
				notificationResponse.setStatus(200);
				notificationResponse.setMessage("Got the List for customerID " + customerID);
				notificationResponse.setListData(notificationList);
			} else {
				notificationResponse.setStatus(400);
				notificationResponse.setMessage("CustomerId not found");
				notificationResponse.setData(null);
			}
		}
		return new ResponseEntity<>(notificationResponse, HttpStatus.OK);
	}

	@PostMapping("/updateAccountNumber/{customerId}/{accountNumber}")
	public ResponseEntity<Void> updateAmount(@PathVariable Integer customerId, @PathVariable Long accountNumber) {
		customerRegisterServiceImpl.updateNotifications(customerId, accountNumber);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/getCustomerDetails/{customerId}")
	public ResponseEntity<CustomerDTO> getCustomerDetail(@PathVariable int customerId) {
		Customer customer = customerRepository.findByCustomerId(customerId);
		Address address = addressRepository.findByCustomerId(customerId);
		AddressDTO addressDTO = new AddressDTO();

		addressDTO.setCity(address.getCity());
		addressDTO.setState(address.getState());
		addressDTO.setStreet(address.getStreet());
		addressDTO.setZipcode(address.getZipcode());

		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setFirstName(customer.getFirstName());
		customerDTO.setLastName(customer.getLastName());
		customerDTO.setAadharNumber(customer.getAadharNumber());
		customerDTO.setAddressDTO(addressDTO);
		customerDTO.setDateOfBirth(customer.getDateOfBirth());
		customerDTO.setEmail(customer.getEmail());
		customerDTO.setGender(customer.getGender());
		customerDTO.setPanNumber(customer.getPanNumber());

		return new ResponseEntity<>(customerDTO, HttpStatus.OK);

	}

	/**
	 * @author nirajku Reset Password functionality and their needed controller.
	 */
 
	/*********************/
 
	/**
	 * This controller will be first triggered after clicking forgot password at
	 * next step it will be setting token for that account after that it will send
	 * the mail with the token
	 */
 
	/**
	 * Endpoint to validate user information and send reset password link.
	 * 
	 * @param forgotPasswordDTO The data transfer object containing user
	 *                          information.
	 * @return ResponseEntity with CustomerResponse indicating the result of the
	 *         operation.
	 */
	@Operation(summary = "Forgot Password")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Customer found"),
			@ApiResponse(responseCode = "404", description = "Invalid data or user not found") })
	@PostMapping("/forgot-password")
	public ResponseEntity<CustomerResponse> validateUserToRetrievePassword(
			@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
		log.info("Inside validateUser function of class ForgotController for validating the details  ");
		return ResponseEntity.ok(customerRegisterServiceImpl.validateUserToRetrievePassword(
				forgotPasswordDTO.getUserId(), forgotPasswordDTO.getPan(), forgotPasswordDTO.getDateOfBirth(),
				forgotPasswordDTO.getPhoneNumber()));
	}
 
	/**
	 * @author nirajku
	 * This controller will validate the token after clicking on the Link sent if
	 * token is valid and exist in account then it will be sending it to the reset
	 * password page If the given token is not valid then it will be providing some
	 * error message and it will do not display the reset password page
	 */
	@Operation(summary = "Reset Password Token Validaion")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Token is valid"),
			@ApiResponse(responseCode = "401", description = "Token is Expired"),
			@ApiResponse(responseCode = "404", description = "Invalid Token") })
	@GetMapping("/validate/{token}")
	public ResponseEntity<CustomerResponse> validateToken(@PathVariable String token) {
		log.info("Inside the controller of Reset password token validation  ");
		return ResponseEntity.ok(customerRegisterServiceImpl.validateToken(token));
	}
 
	/**
	 * @author nirajku
	 * This controller will validate the token after submitting the new password
	 * if token is valid then it will reset the password by calling restAPI from Login service
	 * @param passwordtoken It is the generated token which is matched to teh customer
	 * once it reseted the password it will remove the token for that customer
	 */
	@Operation(summary = "reseting the password")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password Reseted Successfully"),
			@ApiResponse(responseCode = "401", description = "New password cannot be the same as the old password"),
			@ApiResponse(responseCode = "404", description = "Token is Expired"),
			@ApiResponse(responseCode = "400", description = "Old password and new password cannot be same"),
			@ApiResponse(responseCode = "422", description = "Password not reseted")})
 
	@PostMapping("/reset-password/{passwordtoken}")
	public ResponseEntity<CustomerResponse> resetPassword(@PathVariable String passwordtoken,
			@RequestBody String newPassword) {
		log.info("Inside the controller to reset the password on the validation of Token");
		return ResponseEntity.ok(customerRegisterServiceImpl.resetPassword(passwordtoken, newPassword));
	}
}
