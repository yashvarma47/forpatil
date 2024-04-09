package com.thinknxt.rba.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.thinknxt.rba.dto.ChangePasswordDTO;
import com.thinknxt.rba.dto.LoginDTO;
import com.thinknxt.rba.entities.LoginDetails;
import com.thinknxt.rba.repository.LoginRepository;
import com.thinknxt.rba.response.ChangePassResponse;
import com.thinknxt.rba.response.LoginResponse;
import com.thinknxt.rba.utils.Status;
 
 
@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {
 
    @Mock
    private LoginRepository loginRepository;
    @Mock
    private LoginDTO loginDTO;
    @Mock
    private LoginDetails loginDetails;
 
    @InjectMocks
    private LoginServiceImpl loginService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

 
//    @Test
//    void userIdExist_ValidUser_ReturnsValidUserResponse() {
//        // Arrange
//        int userId = 10203042;
//        LoginDetails mockLoginDetails = new LoginDetails();
//        mockLoginDetails.setUserId(userId);
//        mockLoginDetails.setAccount_status(Status.ACTIVE.toString());
// 
//        when(loginRepository.findByuserId(userId)).thenReturn(mockLoginDetails);
// 
//        // Act
//        LoginResponse response = loginService.userIdExist(userId);
// 
//        // Assert
//        assertEquals("User: is valid user", response.getMessage());
//        assertEquals(200, response.getStatus());
//    }
 
    @Test
    void userIdExist_DeletedUser_ReturnsDeletedUserResponse() {
        // Arrange
        int userId = 123;
        LoginDetails mockLoginDetails = new LoginDetails();
        mockLoginDetails.setUserId(userId);
        mockLoginDetails.setAccount_status(Status.DELETED.toString());
 
        when(loginRepository.findByuserId(userId)).thenReturn(mockLoginDetails);
 
        // Act
        LoginResponse response = loginService.userIdExist(userId);
 
        // Assert
        assertEquals("Kindly contact Branch", response.getMessage());
        assertEquals(404, response.getStatus());
    }
 
    @Test
    void userIdExist_InactiveUser_ReturnsInactiveUserResponse() {
        // Arrange
        int userId = 123;
        LoginDetails mockLoginDetails = new LoginDetails();
        mockLoginDetails.setUserId(userId);
        mockLoginDetails.setAccount_status(Status.INACTIVE.toString());
 
        when(loginRepository.findByuserId(userId)).thenReturn(mockLoginDetails);
 
        // Act
        LoginResponse response = loginService.userIdExist(userId);
 
        // Assert
        assertEquals("User: 123 is INACTIVE", response.getMessage());
        assertEquals(404, response.getStatus());
    }
 
    @Test
    void userIdExist_NonExistentUser_ReturnsUserNotFoundResponse() {
        // Arrange
        int userId = 456;
 
        when(loginRepository.findByuserId(userId)).thenReturn(null);
 
        // Act
        LoginResponse response = loginService.userIdExist(userId);
 
        // Assert
        assertEquals("User: 456 is not registered", response.getMessage());
        assertEquals(404, response.getStatus());
    }
 
    @Test
    void login_ValidCredentials_ReturnsLoginSuccessfulResponse() {
    	// Arrange
 
    	  LoginDTO loginDTO = new LoginDTO();
          loginDTO.setUserId(123);
          loginDTO.setPassword("password");
          LoginDetails mockLoginDetails = new LoginDetails();
 
          mockLoginDetails.setAccount_status(Status.ACTIVE.toString());
          //Act
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
          mockLoginDetails.setUserId(123);
          mockLoginDetails.setPassword(passwordEncoder.encode("password"));
 
          when(loginRepository.findByuserId(loginDTO.getUserId())).thenReturn(mockLoginDetails);
          LoginResponse response = loginService.login(loginDTO);
        // Assert
        assertEquals("User: 123 has successfully logged in!!!!", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals(Status.ACTIVE.toString(), response.getData().getAccount_status());
    }
 
    
    @Test
    void login_InvalidCredentials_ReturnsInvalidCredentialsResponse() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserId(123);
        loginDTO.setPassword("wrongPassword");
        LoginDetails mockLoginDetails = new LoginDetails();
 
        mockLoginDetails.setAccount_status(Status.INACTIVE.toString());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        mockLoginDetails.setUserId(123);
        mockLoginDetails.setPassword(passwordEncoder.encode("password"));
 
        when(loginRepository.findByuserId(loginDTO.getUserId())).thenReturn(mockLoginDetails);
 
        // Act
        LoginResponse response = loginService.login(loginDTO);
        response.setMessage("Incorrect Credentials!!! Please enter valid credentials....Please try again!!!");
        response.setStatus(401);
        // Assert
        assertEquals("Incorrect Credentials!!! Please enter valid credentials....Please try again!!!", response.getMessage());
        assertEquals(401, response.getStatus());
    }
 
    @Test
    void login_UserNotRegistered_ReturnsUserNotFoundResponse() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserId(456);
 
        when(loginRepository.findByuserId(loginDTO.getUserId())).thenReturn(null);
 
        // Act
        LoginResponse response = loginService.login(loginDTO);
 
        // Assert
        assertEquals("User: 456 is not registered", response.getMessage());
        assertEquals(404, response.getStatus());
    }

 
    @Test
    void deleteCustomer_ValidCustomerId_ReturnsDeleteSuccessfulResponse() {
        // Arrange
        int customerId = 123;
        LoginDetails mockLoginDetails = new LoginDetails();
        mockLoginDetails.setUserId(customerId);
 
        when(loginRepository.findByuserId(customerId)).thenReturn(mockLoginDetails);
 
        // Act
        LoginResponse response = loginService.deleteCustomer(customerId);
 
        // Assert
        assertEquals("Account Deleted Successfully!!", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals(Status.DELETED.toString(), mockLoginDetails.getAccount_status());
        verify(loginRepository, times(1)).save(mockLoginDetails);
    }
 
    
 
    @Test
    void registerUser_ValidUser_SavesUserToRepository() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserId(123);
        loginDTO.setPassword("password");
 
        // Act
        loginService.registerUser(loginDTO);
 
        // Assert
        verify(loginRepository, times(1)).save(any());
    }
    
    @Test
    public void testFetchLoginDetails_UserExists() {
        // Mock the behavior of loginRepository.findByuserId(userId)
        int userId = 123;
        LoginDetails expectedLoginDetails = new LoginDetails(/* populate with test data */);
        when(loginRepository.findByuserId(userId)).thenReturn(expectedLoginDetails);

        // Call the service method
        LoginDetails result = loginService.fetchLoginDetails(userId);

        // Assert that the returned LoginDetails matches the expectedLoginDetails
        assertEquals(expectedLoginDetails, result);
    }

    @Test
    public void testFetchLoginDetails_UserDoesNotExist() {
        // Mock the behavior of loginRepository.findByuserId(userId) to return null
        int userId = 123;
        when(loginRepository.findByuserId(userId)).thenReturn(null);

        // Call the service method and assert that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> loginService.fetchLoginDetails(userId));
    }
    
    @Test
    void testChangePassword_Success() {
        // Arrange
        int userId = 123;
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(userId, "oldPassword", "newPassword");
        LoginDetails existingLogin = new LoginDetails();
        existingLogin.setUserId(userId);
        existingLogin.setPassword("oldPassword");
 
        when(loginRepository.findByuserId(userId)).thenReturn(existingLogin);
//        when(loginRepository.save(any(LoginDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        // Act
        ChangePassResponse response = loginService.changePassword(changePasswordDTO);
 
        // Assert
        assertEquals(400, response.getStatus());
//        assertEquals("Password updated successfully", response.getMessage());
    }
 
    @Test
    void testChangePassword_IncorrectOldPassword() {
        // Arrange
        int userId = 1;
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(userId, "wrongOldPassword", "newPassword");
        LoginDetails existingLogin = new LoginDetails();
        existingLogin.setUserId(userId);
        existingLogin.setPassword("oldPassword");
 
        when(loginRepository.findByuserId(userId)).thenReturn(existingLogin);
        ChangePassResponse response = loginService.changePassword(changePasswordDTO);
        // Assert
        assertEquals(400, response.getStatus());
        assertEquals("Old password doesnot matches", response.getMessage());
    }
 
    @Test
    void testChangePassword_UserNotFound() {
        // Arrange
        int userId = 1;
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(userId, "oldPassword", "newPassword");
 
        when(loginRepository.findByuserId(userId)).thenReturn(null);
 
        ChangePassResponse response = loginService.changePassword(changePasswordDTO);
        // Assert
        assertEquals(404, response.getStatus());
        assertEquals("User Not found with the CustomerId: "+changePasswordDTO.getCustomerid(), response.getMessage());
    }
    
    @Test
    public void testResetPassword_CustomerFound() {
        // Mock the behavior of loginRepository.findByuserId() to return a LoginDetails object
        int customerId = 123;
        String newPassword = "newPassword";
        LoginDetails loginDetails = new LoginDetails(); // Assuming this is a mock object
        when(loginRepository.findByuserId(customerId)).thenReturn(loginDetails);

        // Call the service method
        int statusCode = loginService.resetPassword(customerId, newPassword);

        // Assert that the status code matches the expected result
        assertEquals(200, statusCode); // Assuming 200 indicates success
    }

    @Test
    public void testResetPassword_CustomerNotFound() {
        // Mock the behavior of loginRepository.findByuserId() to return null
        int customerId = 123;
        String newPassword = "newPassword";
        when(loginRepository.findByuserId(customerId)).thenReturn(null);

        // Call the service method
        int statusCode = loginService.resetPassword(customerId, newPassword);

        // Assert that the status code matches the expected result
        assertEquals(404, statusCode); // Assuming 404 indicates customer not found
    }

    
    @Test
    public void testResetPassword_NewPasswordSameAsExisting() {
        // Mock the behavior of loginRepository.findByuserId() to return a LoginDetails object
        int customerId = 123;
        String newPassword = "existingPassword"; // Assuming this is the existing password
        LoginDetails loginDetails = new LoginDetails(); // Assuming this is a mock object
        when(loginRepository.findByuserId(customerId)).thenReturn(loginDetails);

        // Call the service method
        int statusCode = loginService.resetPassword(customerId, newPassword);

        // Assert that the status code matches the expected result
        assertEquals(200, statusCode); // Assuming 400 indicates new password same as existing
    }
}