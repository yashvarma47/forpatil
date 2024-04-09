package com.thinknxt.rba.controllers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 
import com.thinknxt.rba.dto.AddressDTO;
import com.thinknxt.rba.dto.CustomerDTO;
import com.thinknxt.rba.dto.ForgotIdDTO;
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
 
@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {
    @Mock
    private CustomerRegisterServiceImpl customerService;
    @InjectMocks
    private CustomerController customerController;
    @Mock
	private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerRegisterServiceImpl customerRegisterServiceImpl;
    @Mock
	private AddressRepository addressRepository;
    @Mock
    private NotificationsRepository notificationsRepository;
    @BeforeEach
    public void setUp() {
        // Initialize the mocks
    	this.addressRepository=addressRepository;
    	this.customerRepository=customerRepository;
        MockitoAnnotations.initMocks(this);
    }
 

//    @Test
//    public void testRegisterCustomer_Success() {
//        // Arrange
//        CustomerDTO customerDTO = new CustomerDTO();
//        when(customerService.registerCustomer(any())).thenReturn(new CustomerResponse("Customer registered successfully", 201, null));
// 
//        // Act
//        ResponseEntity<CustomerResponse> response = customerController.registerCustomer(customerDTO);
// 
//        // Assert
//        verify(customerService, times(1)).registerCustomer(any());
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Customer registered successfully", response.getBody().getMessage());
//        assertEquals(201, response.getBody().getStatus());
//        assertEquals(null, response.getBody().getData());
//    }
    @Test
    public void testRegisterCustomer_Failure() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        when(customerService.registerCustomer(any())).thenReturn(new CustomerResponse("Error in registering customer", 500, null));
        // Act
        ResponseEntity<CustomerResponse> response = customerController.registerCustomer(customerDTO);
        // Assert
        verify(customerService, times(1)).registerCustomer(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Error in registering customer", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
        assertEquals(null, response.getBody().getData());
    }
    @Test
    public void testGetCustomerInfo_Success() {
        // Arrange
        int customerId = 1;
        String email = "test@example.com";
        String phoneNumber = "1234567890";
        when(customerService.getCustomerOnIdEmailAndPhoneNumber(customerId, email, phoneNumber))
                .thenReturn(new CustomerResponse("Customer information retrieved successfully", 200, new Customer()));
        // Act
        ResponseEntity<CustomerResponse> response = customerController.getCustomerInfo(customerId, email, phoneNumber);
        // Assert
        verify(customerService, times(1)).getCustomerOnIdEmailAndPhoneNumber(customerId, email, phoneNumber);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer information retrieved successfully", response.getBody().getMessage());
        assertEquals(200, response.getBody().getStatus());
        assertEquals(new Customer(), response.getBody().getData());
    }
    @Test
    public void testGetCustomerInfo_Failure() {
        // Arrange
        int customerId = 1;
        String email = "test@example.com";
        String phoneNumber = "1234567890";
        when(customerService.getCustomerOnIdEmailAndPhoneNumber(customerId, email, phoneNumber))
                .thenReturn(new CustomerResponse("Customer not found", 404, null));
        // Act
        ResponseEntity<CustomerResponse> response = customerController.getCustomerInfo(customerId, email, phoneNumber);
        // Assert
        verify(customerService, times(1)).getCustomerOnIdEmailAndPhoneNumber(customerId, email, phoneNumber);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertEquals(null, response.getBody().getData());
    }
    @Test
    public void testValidateUser_Success() {
        // Arrange
        ForgotIdDTO forgotIdDTO = new ForgotIdDTO("aadhar123", "pan123", Date.valueOf("2000-01-01"), "1234567890");
        when(customerService.validateUserToRetrieveUserId(any(), any(), any(), any())).thenReturn(new CustomerResponse("User validated successfully", 200, null));
        // Act
        ResponseEntity<CustomerResponse> response = customerController.validateUserToRetrieveUserId(forgotIdDTO);
        // Assert
        verify(customerService, times(1)).validateUserToRetrieveUserId(any(), any(), any(), any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User validated successfully", response.getBody().getMessage());
        assertEquals(200, response.getBody().getStatus());
        assertEquals(null, response.getBody().getData());
    }
    @Test
    public void testValidateUser_Failure() {
        // Arrange
        ForgotIdDTO forgotIdDTO = new ForgotIdDTO("aadhar123", "pan123", Date.valueOf("2000-01-01"), "1234567890");
        when(customerService.validateUserToRetrieveUserId(any(), any(), any(), any())).thenReturn(new CustomerResponse("User validation failed", 404, null));
        // Act
        ResponseEntity<CustomerResponse> response = customerController.validateUserToRetrieveUserId(forgotIdDTO);
        // Assert
        verify(customerService, times(1)).validateUserToRetrieveUserId(any(), any(), any(), any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User validation failed", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertEquals(null, response.getBody().getData());
    }

    @Test
    public void testGetAllCustomers_Success() {
        // Mock data
    	List<Customer> mockCustomers = Arrays.asList(
    			new Customer(10203042, "Vipul", "Choudhari", Date.valueOf("1999-01-01"), "Male", "vipul@gmail.com", "8123456789", "ABCRW1234J", "123456789012", null)
    			);
 
        // Mock the service method
        when(customerService.getAllCustomers()).thenReturn(mockCustomers);
 
        // Call the controller method
        ResponseEntity<AllCustomerResponse<List<Customer>>> responseEntity = customerController.getAllCustomers();
 
        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Customers retrieved successfully", responseEntity.getBody().getMessage());
        assertEquals(200, responseEntity.getBody().getStatus());
        assertEquals(mockCustomers, responseEntity.getBody().getData());
    }
 
    @Test
    public void testGetAllCustomers_NoCustomersFound() {
        // Mock the service method to return an empty list
        when(customerController.getAllCustomers()).thenReturn(null);
 
        // Call the controller method
        ResponseEntity<AllCustomerResponse<List<Customer>>> responseEntity = customerController.getAllCustomers();
 
        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Customers retrieved successfully", responseEntity.getBody().getMessage());
        assertEquals(null, responseEntity.getBody().getData());
    }
 
    @Test
    void testValidateUserToRetrieveUserId_CustomerFound() {
        // Arrange
        ForgotIdDTO forgotIdDTO = new ForgotIdDTO("pan", "aadhar", Date.valueOf("2000-01-01"), "phoneNumber");
        CustomerResponse expectedResponse = new CustomerResponse("Customer found", 200, null);
        when(customerService.validateUserToRetrieveUserId(anyString(), anyString(), any(Date.class), anyString()))
            .thenReturn(expectedResponse);
 
        // Act
        CustomerResponse responseEntity = customerService.validateUserToRetrieveUserId(forgotIdDTO.getPan(), forgotIdDTO.getAadhar(), forgotIdDTO.getDateOfBirth(), forgotIdDTO.getPhoneNumber());
 
        // Assert
        assertEquals(200, responseEntity.getStatus());
    }
 
    @Test
    void testValidateUserToRetrieveUserId_CustomerNotFound() {
        // Arrange
        ForgotIdDTO forgotIdDTO = new ForgotIdDTO("pan", "aadhar", Date.valueOf("2000-01-01"), "phoneNumber");
        CustomerResponse expectedResponse = new CustomerResponse("Invalid data or user not found", 404, null);
        when(customerService.validateUserToRetrieveUserId(anyString(), anyString(), any(Date.class), anyString()))
            .thenReturn(expectedResponse);
 
        // Act
        CustomerResponse responseEntity = customerService.validateUserToRetrieveUserId(forgotIdDTO.getPan(), forgotIdDTO.getAadhar(), forgotIdDTO.getDateOfBirth(), forgotIdDTO.getPhoneNumber());
 
        assertEquals(404, responseEntity.getStatus());
    }
    @Test
    void testRegisterCustomer_Success() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO("John", "Doe", new AddressDTO("Street", "City", "State", "123456"),
                Date.valueOf("2000-01-01"), "Male", "john@example.com", "1234567890", "ABCDE1234F", "123456789012");
        CustomerResponse expectedResponse = new CustomerResponse("Customer: 0 has been registered successfully!!!!", 201, null);
        when(customerService.registerCustomer(any(CustomerDTO.class))).thenReturn(expectedResponse);
 
        // Act
        ResponseEntity<CustomerResponse> responseEntity = customerController.registerCustomer(customerDTO);
 
        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }
    @Test
    public void testUpdateCustomerDetails() {
        // Prepare test data
        int customerId = 1;
        CustomerDTO updatedCustomer = new CustomerDTO();
        updatedCustomer.setFirstName("John");
        updatedCustomer.setLastName("Doe");
        updatedCustomer.setDateOfBirth(new Date(System.currentTimeMillis()));
        updatedCustomer.setGender("Male");
        updatedCustomer.setEmail("john.doe@example.com");
        updatedCustomer.setPhoneNumber("1234567890");
        updatedCustomer.setPanNumber("ABCDE1234F");
        updatedCustomer.setAadharNumber("123456789012");
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("123 Main St");
        addressDTO.setCity("Anytown");
        addressDTO.setState("AnyState");
        addressDTO.setZipcode("12345");
        updatedCustomer.setAddressDTO(addressDTO);
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerId(customerId);
        existingCustomer.setFirstName("Jane");
        existingCustomer.setLastName("Doe");
        existingCustomer.setDateOfBirth(new Date(System.currentTimeMillis()));
        existingCustomer.setGender("Female");
        existingCustomer.setEmail("jane.doe@example.com");
        existingCustomer.setPhoneNumber("9876543210");
        existingCustomer.setPanNumber("FGHIJ5678K");
        existingCustomer.setAadharNumber("987654321098");
        Address address = new Address();
        address.setStreet("456 Elm St");
        address.setCity("Anytown");
        address.setState("AnyState");
        address.setZipcode("54321");
        existingCustomer.setAddress(address);
        // Mock repository method
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        // Call the service method
        ResponseEntity<CustomerResponse> responseEntity = customerRegisterServiceImpl.updateCustomerDetails(customerId, updatedCustomer);
        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Customer updated successfully", responseEntity.getBody().getMessage());
        assertEquals(customerId, responseEntity.getBody().getData().getCustomerId());
        assertEquals(updatedCustomer.getFirstName(), responseEntity.getBody().getData().getFirstName());
        assertEquals(updatedCustomer.getLastName(), responseEntity.getBody().getData().getLastName());
        assertEquals(updatedCustomer.getDateOfBirth(), responseEntity.getBody().getData().getDateOfBirth());
        assertEquals(updatedCustomer.getGender(), responseEntity.getBody().getData().getGender());
        assertEquals(updatedCustomer.getEmail(), responseEntity.getBody().getData().getEmail());
        assertEquals(updatedCustomer.getPhoneNumber(), responseEntity.getBody().getData().getPhoneNumber());
        assertEquals(updatedCustomer.getPanNumber(), responseEntity.getBody().getData().getPanNumber());
        assertEquals(updatedCustomer.getAadharNumber(), responseEntity.getBody().getData().getAadharNumber());
        assertEquals(updatedCustomer.getAddressDTO().getStreet(), responseEntity.getBody().getData().getAddress().getStreet());
        assertEquals(updatedCustomer.getAddressDTO().getCity(), responseEntity.getBody().getData().getAddress().getCity());
        assertEquals(updatedCustomer.getAddressDTO().getState(), responseEntity.getBody().getData().getAddress().getState());
        assertEquals(updatedCustomer.getAddressDTO().getZipcode(), responseEntity.getBody().getData().getAddress().getZipcode());
    }

 
    @Test
    public void getCustomerDetailTest() {
        // Arrange
        int customerId = 1;
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setAadharNumber("1234567890");
   //     customer.setDateOfBirth("1990-01-01");
        customer.setEmail("john.doe@example.com");
        customer.setGender("Male");
        customer.setPanNumber("ABCDE1234F");
 
        Address address = new Address();
        address.setCustomerId(customerId);
        address.setCity("City");
        address.setState("State");
        address.setStreet("Street");
        address.setZipcode("123456");
 
        when(customerRepository.findByCustomerId(Mockito.eq(customerId))).thenReturn(customer);
        when(addressRepository.findByCustomerId(Mockito.eq(customerId))).thenReturn(address);
 
        // Act
        ResponseEntity<CustomerDTO> responseEntity = customerController.getCustomerDetail(customerId);
 
        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
 
        CustomerDTO customerDTO = responseEntity.getBody();
        assertEquals("John", customerDTO.getFirstName());
        assertEquals("Doe", customerDTO.getLastName());
        assertEquals("1234567890", customerDTO.getAadharNumber());
        assertEquals(null, customerDTO.getDateOfBirth());
        assertEquals("john.doe@example.com", customerDTO.getEmail());
        assertEquals("Male", customerDTO.getGender());
        assertEquals("ABCDE1234F", customerDTO.getPanNumber());
        assertEquals("City", customerDTO.getAddressDTO().getCity());
        assertEquals("State", customerDTO.getAddressDTO().getState());
        assertEquals("Street", customerDTO.getAddressDTO().getStreet());
        assertEquals("123456", customerDTO.getAddressDTO().getZipcode());
 
        verify(customerRepository, times(1)).findByCustomerId(Mockito.eq(customerId));
        verify(addressRepository, times(1)).findByCustomerId(Mockito.eq(customerId));
    }
    @Test
    public void testGetNotificationDataWithValidAccountNumber() {
        Long accountNumber = 123456L;
        Notifications notification = new Notifications();
        notification.setAccountNumber(accountNumber);
        when(notificationsRepository.findByAccountNumber(accountNumber)).thenReturn(notification);
 
        ResponseEntity<NotificationResponse> responseEntity = customerController.getNotificationData(0, accountNumber);
 
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("got data for account number " + accountNumber, responseEntity.getBody().getMessage());
        assertEquals(notification, responseEntity.getBody().getData());
    }
 
    @Test
    public void testGetNotificationDataWithInvalidAccountNumber() {
        Long accountNumber = 123456L;
        when(notificationsRepository.findByAccountNumber(accountNumber)).thenReturn(null);
 
        ResponseEntity<NotificationResponse> responseEntity = customerController.getNotificationData(0, accountNumber);
 
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Account not found", responseEntity.getBody().getMessage());
        assertEquals(null, responseEntity.getBody().getData());
    }
 
    @Test
    public void testGetNotificationDataWithValidCustomerId() {
        Integer customerID = 123;
        List<Notifications> notificationList = new ArrayList<>();
        notificationList.add(new Notifications());
        when(notificationsRepository.findAllByCustomerId(customerID)).thenReturn(notificationList);
 
        ResponseEntity<NotificationResponse> responseEntity = customerController.getNotificationData(customerID, 0l);
 
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Got the List for customerID " + customerID, responseEntity.getBody().getMessage());
        assertEquals(notificationList, responseEntity.getBody().getListData());
    }
 
    @Test
    public void testGetNotificationDataWithInvalidCustomerId() {
        Integer customerID = 123;
        when(notificationsRepository.findAllByCustomerId(customerID)).thenReturn(null);
 
        ResponseEntity<NotificationResponse> responseEntity = customerController.getNotificationData(customerID, 0l);
 
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("CustomerId not found", responseEntity.getBody().getMessage());
        assertEquals(null, responseEntity.getBody().getData());
    }
    @Test
    public void testUpdateAccount() {
        // Call the controller method
        ResponseEntity<Void> responseEntity =
        		customerController.updateAmount(1, 123456789L);
 
        // Assert the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateNotificationTxn_Confirmation() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "Email";
        String notificationType = "Txn_Confirmation";
 
        // Mock the repository method
 
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforTxnConfirmation(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Email will be sent to your registered emailID", responseEntity.getBody().getMessage());
        assertEquals(200, responseEntity.getBody().getStatus());
    }
    @Test
    public void testUpdateNotificationSecurity_alerts() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "Email";
        String notificationType = "Security_alerts";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforSecurityAlerts(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Email will be sent to your registered emailID", responseEntity.getBody().getMessage());
        assertEquals(200, responseEntity.getBody().getStatus());
    }
 
    @Test
    public void testUpdateNotificationAccountBalanceUpdates() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "Email";
        String notificationType = "Account_balance_updates";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforAccountBalanceUpdates(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Email will be sent to your registered emailID", responseEntity.getBody().getMessage());
        assertEquals(200, responseEntity.getBody().getStatus());
    }
    @Test
    public void testUpdateNotificationLowBalanceAlerts() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "Email";
        String notificationType = "Low_balance_alerts";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforLowBalanceAlerts(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Email will be sent to your registered emailID", responseEntity.getBody().getMessage());
        assertEquals(200, responseEntity.getBody().getStatus());
    }
 
    @Test
    public void testUpdateNotificationAccountBlockAlerts() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "Email";
        String notificationType = "Account_block_alerts";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforAccountBlockAlerts(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Email will be sent to your registered emailID", responseEntity.getBody().getMessage());
        assertEquals(200, responseEntity.getBody().getStatus());
    }
 
    @Test
    public void testUpdateNotificationTxn_ConfirmationNA() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "NA";
        String notificationType = "Txn_Confirmation";
 
        // Mock the repository method
 
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforTxnConfirmationNA(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("You will stop recieving email from now onwards.", responseEntity.getBody().getMessage());
        assertEquals(302, responseEntity.getBody().getStatus());
    }
    @Test
    public void testUpdateNotificationSecurity_alertsNA() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "NA";
        String notificationType = "Security_alerts";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforSecurityAlertsNA(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("You will stop recieving email from now onwards.", responseEntity.getBody().getMessage());
        assertEquals(302, responseEntity.getBody().getStatus());
    }
 
    @Test
    public void testUpdateNotificationAccountBalanceUpdatesNA() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "NA";
        String notificationType = "Account_balance_updates";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforAccountBalanceUpdatesNA(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("You will stop recieving email from now onwards.", responseEntity.getBody().getMessage());
        assertEquals(302, responseEntity.getBody().getStatus());
    }
    @Test
    public void testUpdateNotificationLowBalanceAlertsNA() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "NA";
        String notificationType = "Low_balance_alerts";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforLowBalanceAlertsNA(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("You will stop recieving email from now onwards.", responseEntity.getBody().getMessage());
        assertEquals(302, responseEntity.getBody().getStatus());
    }
 
    @Test
    public void testUpdateNotificationAccountBlockAlertsNA() {
        // Set up test data
        int customerId = 1;
        String notificationVia = "NA";
        String notificationType = "Account_block_alerts";
 
        
        // Call the controller method
        ResponseEntity<NotificationResponse> responseEntity =
        		customerController.updateNotifications(notificationVia, notificationType, customerId);
 
        // Verify repository method was called
        verify(notificationsRepository).updateAllByCustomerIdforAccountBlockAlertsNA(notificationVia, customerId);
 
        // Verify response status and message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("You will stop recieving email from now onwards.", responseEntity.getBody().getMessage());
        assertEquals(302, responseEntity.getBody().getStatus());
    }
 
}