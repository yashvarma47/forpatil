package com.thinknxt.rba.test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.thinknxt.rba.controllers.LoginController;
import com.thinknxt.rba.response.LoginResponse;
import com.thinknxt.rba.services.LoginServiceImpl;

public class LoginControllerTest {
	
	@InjectMocks
	private LoginController loginController;
	
	@Mock
    private LoginServiceImpl loginServiceImpl;
	
	@Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        loginServiceImpl = mock(LoginServiceImpl.class);
        restTemplate = mock(RestTemplate.class);
        loginController = new LoginController(loginServiceImpl, restTemplate);
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
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(""));

        
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
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(""));

        
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
        
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ResponseEntity<LoginResponse> response = loginController.deleteCustomer(customerId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The customer cannot be deleted as there are accounts associated with their profile. "
                + "To proceed, please close the associated accounts first.", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
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
}
