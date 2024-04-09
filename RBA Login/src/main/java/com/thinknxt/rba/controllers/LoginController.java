package com.thinknxt.rba.controllers;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.thinknxt.rba.dto.ChangePasswordDTO;
import com.thinknxt.rba.dto.LoginDTO;
import com.thinknxt.rba.entities.LoginDetails;
import com.thinknxt.rba.response.ChangePassResponse;
import com.thinknxt.rba.response.LoginResponse;
import com.thinknxt.rba.services.LoginServiceImpl;
import com.thinknxt.rba.utils.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller class for handling login-related operations.
 */
@RestController
@Validated
@RequestMapping("/login")
@Slf4j
public class LoginController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private LoginServiceImpl loginServiceImpl;

	// Injecting RestTemplate bean
	private final RestTemplate restTemplate;

	public LoginController(LoginServiceImpl loginServiceImpl, RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.loginServiceImpl = loginServiceImpl;
	}

	/**
	 * @author vipulp
	 * Validate the existence of a user based on the provided userId.
	 *
	 * @param loginDTO The data transfer object containing userId information.
	 * @return ResponseEntity with the result of the validation operation.
	 */
	@Operation(summary = "Validate User ID", description = "Check if the provided User ID is valid.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User ID is valid"),
			@ApiResponse(responseCode = "400", description = "User ID cannot be empty or null") })
	@PostMapping("/userId")
	public ResponseEntity<LoginResponse> validateUserId(@RequestBody LoginDTO loginDTO) {
		log.info("Inside validateUserId function of LoginController class ");
		int userId = loginDTO.getUserId();
		if (NumberUtils.INTEGER_ZERO.equals(userId)) {
			log.info("if part Inside validateUserId function of LoginController class ");
			return ResponseEntity.badRequest()
					.body(new LoginResponse("User Id cannot be empty or null!!!", 400, null, null));
		} else {
			log.info("else part Inside validateUserId function of LoginController class ");
			LoginResponse loginResponse = loginServiceImpl.userIdExist(userId);
			return new ResponseEntity<>(loginResponse, HttpStatus.OK);
		}
	}

	/**
	 * Perform user login and generate a JWT token upon successful authentication.
	 *
	 * @param loginDTO The data transfer object containing login credentials.
	 * @return ResponseEntity with the result of the login operation and JWT token.
	 * @throws Exception if there's an issue with the authentication process.
	 */
	@Operation(summary = "Login authenticated user", description = "Authenticate and generate a JWT token for the user.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Login successful"),
			@ApiResponse(responseCode = "400", description = "User ID cannot be empty or null"),
			@ApiResponse(responseCode = "401", description = "Unauthorized") })
	@PostMapping("/loginUser")
	public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginDTO loginDTO) throws Exception {
		log.info("Inside login function of LoginController class ");
		int userId = loginDTO.getUserId();
		LoginResponse loginResponse = loginServiceImpl.login(loginDTO);
		if (NumberUtils.INTEGER_ZERO.equals(userId)) {
			return ResponseEntity.badRequest()
					.body(new LoginResponse("User Id cannot be empty or null!!!", 400, null, null));
		} else {
			if (loginResponse.getStatus() == 200) {
				log.info("if part Inside login function of LoginController class ");
				try {
					log.info("try block of if part Inside login function of LoginController class ");

					authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword()));
				} catch (Exception ex) {
					log.info("Catch block of if part Inside login function of LoginController class ");
					throw new Exception("Invalid Credentials!!!");
				}
				String jwtToken = jwtUtil.generateToken(String.valueOf(loginDTO.getUserId()));

				loginResponse.setJwtToken(jwtToken);
				log.info("End of if part Inside login function of LoginController class ");
				return new ResponseEntity<>(loginResponse, HttpStatus.OK);
			} else
				log.info("else part Inside login function of LoginController class ");
			return new ResponseEntity<>(loginResponse, HttpStatus.UNAUTHORIZED);
		}

	}

	/**
	 * Register a new user with the provided login credentials.
	 *
	 * @param loginDTO The data transfer object containing user registration
	 *                 information.
	 * @return ResponseEntity indicating the result of the user registration
	 *         operation.
	 */
	@PostMapping("/register-user")
	public ResponseEntity<String> registerUser(@RequestBody LoginDTO loginDTO) {
		log.info("Inside registerUser function of LoginController class ");
		log.info("Register-user");
		loginServiceImpl.registerUser(loginDTO);
		return new ResponseEntity<>("Credentials Registered", HttpStatus.CREATED);
	}

	/**
	 * Delete a customer account based on the provided customerId.
	 *
	 * @param customerId The unique identifier for the customer.
	 * @return ResponseEntity with the result of the customer account deletion
	 *         operation.
	 */
	@Operation(summary = "Delete Customer Account")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Customer account deleted successfully"),
			@ApiResponse(responseCode = "404", description = "User ID not found"),
			@ApiResponse(responseCode = "400", description = "Customer cannot be deleted as there are accounts associated") })
	@GetMapping("/customer/delete-customer/{customerId}")
	public ResponseEntity<LoginResponse> deleteCustomer(@PathVariable int customerId) {
		log.info("Inside deleteCustomer function of LoginController class ");

		try {
			if (loginServiceImpl.userIdExist(customerId).getStatus() == 200) {
				log.info("if part Inside deleteCustomer function of LoginController class ");

				try {
					restTemplate.getForEntity(
							"http://localhost:1012/api/retailbanking/accounts/getaccount/" + customerId, String.class);

					LoginResponse loginResponse = loginServiceImpl.deleteCustomer(customerId);
					return ResponseEntity.ok(new LoginResponse("Deleted successfully", 200, null, null));

				} catch (HttpClientErrorException ex) {
					// If accounts are mapped, return a specific error response
					if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
						log.info("Mapped accounts found. Cannot delete the customer.");
						return ResponseEntity.badRequest().body(new LoginResponse(
								"The customer cannot be deleted as there are accounts associated with their profile. "
										+ "To proceed, please close the associated accounts first.",
								400, null, null));
					} else {
						throw ex;
					}
				}

			} else {
				log.info("else part Inside deleteCustomer function of LoginController class ");
				return ResponseEntity.badRequest().body(new LoginResponse("User Id not found!!", 404, null, null));
			}
		} catch (Exception ex) {
			// Handle other exceptions
			log.error("Error while processing deleteCustomer for customer ID: {}", customerId, ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new LoginResponse("Error while processing deleteCustomer", 500, null, null));
		}
	}

	/**
	 * Change Password of user based on the provided customerId.
	 *
	 * @param customerId The unique identifier for the customer.
	 * @return ChangePasswordResponse with the status code and the message.
	 */

	@Operation(summary = "Change password")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password Change successfully"),
			@ApiResponse(responseCode = "404", description = "User ID not found"),
			@ApiResponse(responseCode = "400", description = "Old password doesnot match") })
	@PostMapping("/change-password/{customerId}")
	public ResponseEntity<ChangePassResponse> changePassword(@PathVariable int customerId,
			@RequestParam String oldPassword, @RequestParam String newPassword) {
		log.info("Inside the API of Changing password");

		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setCustomerid(customerId);
		changePasswordDTO.setOldPassword(oldPassword);
		changePasswordDTO.setNewPassword(newPassword);

		ChangePassResponse response = loginServiceImpl.changePassword(changePasswordDTO);
		log.info("End of API of Changing password");
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	
	//	@PreAuthorize("hasAuthority('ADMIN')")
	/**
	 ** @author sidheshwar
	 * */
	@GetMapping("/fetchLoginDetails/{userId}")
	public ResponseEntity<LoginDetails> fetchLoginDetails(@PathVariable int userId) {
		LoginDetails loginDetails =loginServiceImpl.fetchLoginDetails(userId);
		return new ResponseEntity<>(loginDetails, HttpStatus.OK);
	}
	
	// Reset password API
		/**
		 * Reset Password of user based on the provided customerId.
		 *
		 * @param customerId The unique identifier for the customer.
		 * @return responseCode according to conditions.
		 */
		@Operation(summary = "reseting the password")
		@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password Reseted Successfully"),
				@ApiResponse(responseCode = "401", description = "New password cannot be the same as the old password"),
				@ApiResponse(responseCode = "404", description = "Token is Expired"),
				@ApiResponse(responseCode = "400", description = "Old password and new password cannot be same"),
				@ApiResponse(responseCode = "422", description = "Password not reseted")})
		@GetMapping("/reset-password/{customerId}")
		public int resetPassword(@PathVariable int customerId, @RequestParam String newPassword) {
			log.info("ResetPassword Controller is called from login using reset template ");
		    int responseCode = loginServiceImpl.resetPassword(customerId, newPassword);
			log.info("End of ResetPassword Controller and sending response to the rest call");
	 
		    return responseCode;
		}
}
