package com.thinknxt.rba.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.thinknxt.rba.controllers.LoginController;
import com.thinknxt.rba.dto.ChangePasswordDTO;
import com.thinknxt.rba.dto.LoginDTO;
import com.thinknxt.rba.entities.LoginDetails;
import com.thinknxt.rba.response.ChangePassResponse;
import com.thinknxt.rba.response.LoginResponse;
import com.thinknxt.rba.services.LoginServiceImpl;
import com.thinknxt.rba.utils.JwtUtil;

public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	private LoginController loginController;

	@Mock
	private LoginServiceImpl loginServiceImpl;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private AuthenticationManager authenticationManager;

	@BeforeEach
	public void setUp() {
		loginServiceImpl = mock(LoginServiceImpl.class);
		restTemplate = mock(RestTemplate.class);
		jwtUtil = mock(JwtUtil.class);
		loginController = new LoginController(loginServiceImpl, restTemplate);
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
	}

	@Test
	public void testDeleteCustomer_Success() {
		// Arrange
		int customerId = 95044155;
		// Mock deleteCustomer to return a LoginResponse
		LoginResponse expectedResponse = new LoginResponse("Deleted successfully", 200, null, null);
		// Mock userIdExist to return ResponseEntity with OK status
		when(loginServiceImpl.userIdExist(customerId)).thenReturn(expectedResponse);

		// Mock restTemplate.getForEntity to simulate successful call
		when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok().body(""));

		when(loginServiceImpl.deleteCustomer(customerId)).thenReturn(expectedResponse);

		// Act
		ResponseEntity<LoginResponse> response = loginController.deleteCustomer(customerId);
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Deleted successfully", response.getBody().getMessage());
		assertEquals(null, response.getBody().getData());
	}

	@Test
	public void testDeleteCustomer_UserNotFound() {
		// Arrange
		int customerId = 123;
		// Mock deleteCustomer to return a LoginResponse
		LoginResponse expectedResponse = new LoginResponse("User Id not found!!", 404, null, null);
		// Mock userIdExist to return ResponseEntity with OK status
		when(loginServiceImpl.userIdExist(customerId)).thenReturn(expectedResponse);

		// Mock restTemplate.getForEntity to simulate successful call
		when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok().body(""));

		when(loginServiceImpl.deleteCustomer(customerId)).thenReturn(expectedResponse);

		ResponseEntity<LoginResponse> response = loginController.deleteCustomer(customerId);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("User Id not found!!", response.getBody().getMessage());
		assertEquals(404, response.getBody().getStatus());
	}

	@Test
	public void testDeleteCustomer_AccountsAssociated() {
		int customerId = 123;

		// Mock deleteCustomer to return a LoginResponse
		LoginResponse expectedResponse = new LoginResponse("Deleted successfully", 200, null, null);
		// Mock userIdExist to return ResponseEntity with OK status
		when(loginServiceImpl.userIdExist(customerId)).thenReturn(expectedResponse);

		when(restTemplate.getForEntity(anyString(), eq(String.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

		ResponseEntity<LoginResponse> response = loginController.deleteCustomer(customerId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Deleted successfully", response.getBody().getMessage());
	}

	@Test
	public void testDeleteCustomer_InternalServerError() {
		int customerId = 123;

		when(loginServiceImpl.userIdExist(customerId)).thenThrow(new RuntimeException("Internal Server Error"));

		ResponseEntity<LoginResponse> response = loginController.deleteCustomer(customerId);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Error while processing deleteCustomer", response.getBody().getMessage());
		assertEquals(500, response.getBody().getStatus());
	}

	@Test
	public void testValidateUserId_Success() {

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setPassword("Test@1234");
		loginDTO.setUserId(95044155);
		// Mock the behavior of userIdExist to return a valid response
		when(loginServiceImpl.userIdExist(123)).thenReturn(new LoginResponse("Valid user ID", 200, null, null));

		// Call the controller method with a valid user ID
		ResponseEntity<LoginResponse> responseEntity = loginController.validateUserId(loginDTO);

		// Verify the response
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testLogin_Success() throws Exception {
		// Mock the behavior of login to return a valid response
		LoginDTO loginDTO = new LoginDTO(123, "password");
		LoginResponse loginResponse = new LoginResponse("Login successful", 200, null, null);
		when(loginServiceImpl.login(loginDTO)).thenReturn(loginResponse);

		// Mock the behavior of authenticationManager.authenticate()
		when(authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword())))
				.thenReturn(null); // Change this as needed

		// Call the controller method with valid login credentials
		ResponseEntity<LoginResponse> responseEntity = loginController.login(loginDTO);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("Login successful", responseEntity.getBody().getMessage());
		assertEquals(200, responseEntity.getBody().getStatus());
	}

	@Test
	public void testRegisterUser_Success() {

		// Call the controller method to register a user
		ResponseEntity<String> responseEntity = loginController.registerUser(new LoginDTO());

		// Verify the response
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertEquals("Credentials Registered", responseEntity.getBody());
	}

	@Test
	public void testFetchLoginDetails_Success() {
		// Mock loginDetails to return a predefined object
		int userId = 123;
		LoginDetails expectedLoginDetails = new LoginDetails(/* populate with test data */);
		when(loginServiceImpl.fetchLoginDetails(userId)).thenReturn(expectedLoginDetails);

		// Call the controller method
		ResponseEntity<LoginDetails> responseEntity = loginController.fetchLoginDetails(userId);

		// Assert the response
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(expectedLoginDetails, responseEntity.getBody());
	}

	@Test
	public void testChangePassword_Success() {
		// Mock the behavior of loginService.changePassword()
		int customerId = 123;
		String oldPassword = "oldPassword";
		String newPassword = "newPassword";
		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(customerId, oldPassword, newPassword);
		ChangePassResponse expectedResponse = new ChangePassResponse(/* populate with test data */);
		when(loginServiceImpl.changePassword(changePasswordDTO)).thenReturn(expectedResponse);

		// Call the controller method
		ResponseEntity<ChangePassResponse> responseEntity = loginController.changePassword(customerId, oldPassword,
				newPassword);

		// Assert that the returned ChangePassResponse matches the expectedResponse
		assertEquals(expectedResponse, responseEntity.getBody());
	}
	
	 
	    @Test
	    public void testResetPassword_PasswordSameAsExisting() throws Exception {
	        // Arrange
	        int customerId = 123;
	        String newPassword = "oldPassword";
	        when(loginServiceImpl.resetPassword(customerId, newPassword)).thenReturn(400);
	 
	        // Act & Assert
	        mockMvc.perform(MockMvcRequestBuilders.get("/reset-password/{customerId}", customerId)
	                .param("newPassword", newPassword)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(MockMvcResultMatchers.status().isNotFound());
	    }
	 
	    @Test
	    public void testResetPassword_CustomerNotFound() throws Exception {
	        // Arrange
	        int customerId = 123;
	        String newPassword = "newPassword";
	        when(loginServiceImpl.resetPassword(customerId, newPassword)).thenReturn(404);
	 
	        // Act & Assert
	        mockMvc.perform(MockMvcRequestBuilders.get("/reset-password/{customerId}", customerId)
	                .param("newPassword", newPassword)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(MockMvcResultMatchers.status().isNotFound());
	    }
	    
	    @Test
	    public void testResetPassword_Success() {
	        // Mock the behavior of loginServiceImpl.resetPassword() to return 200
	        int customerId = 123;
	        String newPassword = "newPassword";
	        when(loginServiceImpl.resetPassword(customerId, newPassword)).thenReturn(200);

	        // Call the controller method
	        int responseCode = loginController.resetPassword(customerId, newPassword);

	        // Assert that the response code matches the expected result
	        assertEquals(200, responseCode);
	    }

	    @Test
	    public void testResetPassword_OldNewPasswordSame() {
	        // Mock the behavior of loginServiceImpl.resetPassword() to return 400
	        int customerId = 123;
	        String newPassword = "oldPassword"; // Assuming this is the same as the old password
	        when(loginServiceImpl.resetPassword(customerId, newPassword)).thenReturn(400);

	        // Call the controller method
	        int responseCode = loginController.resetPassword(customerId, newPassword);

	        // Assert that the response code matches the expected result
	        assertEquals(400, responseCode);
	    }
}
