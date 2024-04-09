package com.thinknxt.rba.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
 
import com.thinknxt.rba.controllers.CustomerController;
import com.thinknxt.rba.dto.AddressDTO;
import com.thinknxt.rba.dto.CustomerDTO;
import com.thinknxt.rba.dto.EmailRequest;
import com.thinknxt.rba.entities.Address;
import com.thinknxt.rba.entities.Customer;
import com.thinknxt.rba.entities.Notifications;
import com.thinknxt.rba.repository.AddressRepository;
import com.thinknxt.rba.repository.CustomerRepository;
import com.thinknxt.rba.repository.NotificationsRepository;
import com.thinknxt.rba.response.CustomerResponse;
@ExtendWith(MockitoExtension.class)
public class CustomerRegisterServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CustomerRegisterServiceImpl customerService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CustomerController customerController;
    @Mock
    private NotificationsRepository notificationsRepository;
    @InjectMocks
    private CustomerRegisterServiceImpl customerRegisterService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Autowired
	private MockMvc mockMvc;
    @BeforeEach
	public void setUp() {
    	customerService = mock(CustomerRegisterServiceImpl.class);
		restTemplate = mock(RestTemplate.class);
		customerController = new CustomerController(customerRegisterService);
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
	}
    @Test
    void testRegisterCustomer_Success() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO("John", "Doe", new AddressDTO("Street", "City", "State", "123456"),
                Date.valueOf("2000-01-01"), "Male", "john@example.com", "1234567890", "ABCDE1234F", "123456789012");
        when(customerRepository.existsByPanNumber(any())).thenReturn(false);
        when(customerRepository.existsByAadharNumber(any())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(new Customer());
        when(addressRepository.save(any(Address.class))).thenReturn(new Address());
        when(passwordEncoder.encode(anyString())).thenReturn("sdsdas");
 
        // Mocking restTemplate.postForEntity() to return a ResponseEntity object
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("Registration successful!", HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponseEntity);
 
        // Act
        CustomerResponse response = customerRegisterService.registerCustomer(customerDTO);
 
        // Assert
        verify(customerRepository, times(1)).existsByPanNumber(any());
        verify(customerRepository, times(1)).existsByAadharNumber(any());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
        assertEquals(200, response.getStatus());
        assertEquals(null, response.getData());
    }
    @Test
    public void testRegisterCustomer_CustomerExists() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO("John", "Doe", new AddressDTO("Street", "City", "State", "123456"),
                Date.valueOf("2000-01-01"), "Male", "john@example.com", "1234567890", "ABCDE1234F", "123456789012");
        when(customerRepository.existsByPanNumber(any())).thenReturn(true);
        // Act
        CustomerResponse response = customerRegisterService.registerCustomer(customerDTO);
        // Assert
        verify(customerRepository, times(1)).existsByPanNumber(any());
        verify(customerRepository, never()).existsByAadharNumber(any());
        verify(customerRepository, never()).save(any(Customer.class));
        verify(addressRepository, never()).save(any(Address.class));
        verify(restTemplate, never()).postForObject(anyString(), any(), eq(String.class));
        assertEquals("This customer already exists!!!!", response.getMessage());
    }
    @Test
    public void testUpdateNotification() {
        // Set up test data
        int customerId = 1;
        long accountNumber = 123456789;
        Notifications notifications = new Notifications();
        notifications.setCustomerId(customerId);
        notifications.setAccountNumber(accountNumber);
        notifications.setAccountBalanceUpdates("NA");
        notifications.setAccountBlockAlerts("NA");
        notifications.setLowBalanceAlerts("NA");
        notifications.setTxnConfirmation("NA");
        notifications.setSecurityAlerts("NA");
 
        // Mock the repository method
        when(notificationsRepository.save(any(Notifications.class)))
                .thenReturn(notifications);
 
        // Call the controller method
        customerRegisterService.updateNotifications(customerId, accountNumber);
 
        // Verify repository method was called
        verify(notificationsRepository).save(notifications);
    }

    @Test
    public void testGetCustomerOnIdEmailAndPhoneNumber_CustomerFound() {
        // Arrange
        when(customerRepository.findByCustomerIdAndEmailAndPhoneNumber(anyInt(), any(), any())).thenReturn(new Customer());
        // Act
        CustomerResponse response = customerRegisterService.getCustomerOnIdEmailAndPhoneNumber(1, "john@example.com", "1234567890");
        // Assert
        verify(customerRepository, times(1)).findByCustomerIdAndEmailAndPhoneNumber(anyInt(), any(), any());
        assertEquals("Customer has been found!!", response.getMessage());
        assertEquals(200, response.getStatus());
    }
    @Test
    public void testGetCustomerOnIdEmailAndPhoneNumber_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByCustomerIdAndEmailAndPhoneNumber(anyInt(), any(), any())).thenReturn(null);
        // Act
        CustomerResponse response = customerRegisterService.getCustomerOnIdEmailAndPhoneNumber(1, "john@example.com", "1234567890");
        // Assert
        verify(customerRepository, times(1)).findByCustomerIdAndEmailAndPhoneNumber(anyInt(), any(), any());
        assertEquals("Customer does not exist!!", response.getMessage());
        assertEquals(400, response.getStatus());
        assertEquals(null, response.getData());
    }    
    @Test
    public void testValidateUserToRetrieveUserId_CustomerFound_Success() {
        // Arrange
        String pan = "ABCDE1234F";
        String aadhar = "123456789012";
        Date dateOfBirth = Date.valueOf("2000-01-01");
        String phoneNumber = "1234567890";
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCustomerId(123);
        when(customerRepository.findByPanNumberAndAadharNumberAndDateOfBirthAndPhoneNumber(pan, aadhar,
                dateOfBirth, phoneNumber)).thenReturn(customer);
        when(restTemplate.postForEntity(anyString(), any(EmailRequest.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("Email sent successfully"));
 
        // Act
        CustomerResponse response = customerRegisterService.validateUserToRetrieveUserId(pan, aadhar, dateOfBirth,
                phoneNumber);
 
        // Assert
        assertEquals("Customer found", response.getMessage());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(null, response.getData());
        verify(restTemplate, times(1)).postForEntity(anyString(), any(EmailRequest.class), eq(String.class));
    }
 
    @Test
    public void testValidateUserToRetrieveUserId_CustomerNotFound_Return404() {
        // Arrange
        String pan = "ABCDE1234F";
        String aadhar = "123456789012";
        Date dateOfBirth = Date.valueOf("2000-01-01");
        String phoneNumber = "1234567890";
        when(customerRepository.findByPanNumberAndAadharNumberAndDateOfBirthAndPhoneNumber(pan, aadhar,
                dateOfBirth, phoneNumber)).thenReturn(null);
 
        // Act
        CustomerResponse response = customerRegisterService.validateUserToRetrieveUserId(pan, aadhar, dateOfBirth,
                phoneNumber);
 
        // Assert
        assertEquals("Invalid data or user not found", response.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(null, response.getData());
    }
    @Test
    public void testGetCustomerById_CustomerExists_Success() {
        // Arrange
        int customerId = 123;
        CustomerRepository customerRepositoryMock = mock(CustomerRepository.class);
        Customer existingCustomer = new Customer();
        when(customerRepositoryMock.findByCustomerId(customerId)).thenReturn(existingCustomer);
 
        CustomerRegisterServiceImpl customerRegisterService = new CustomerRegisterServiceImpl();
        customerRegisterService.setCustomerRepository(customerRepositoryMock, null);
 
        // Act
        CustomerResponse response = customerRegisterService.getCustomerById(customerId);
 
        // Assert
        assertEquals("Customer has been found!!", response.getMessage());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(existingCustomer, response.getData());
    }
 
    @Test
    public void testGetCustomerById_CustomerDoesNotExist_Failure() {
        // Arrange
        int customerId = 456;
        CustomerRepository customerRepositoryMock = mock(CustomerRepository.class);
        when(customerRepositoryMock.findByCustomerId(customerId)).thenReturn(null);
 
        CustomerRegisterServiceImpl customerRegisterService = new CustomerRegisterServiceImpl();
        customerRegisterService.setCustomerRepository(customerRepositoryMock, null);
 
        // Act
        CustomerResponse response = customerRegisterService.getCustomerById(customerId);
 
        // Assert
        assertEquals("Customer does not exist!!", response.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(null, response.getData());
    }
    @Test
    void testGetAllCustomers_Success() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(/* add customer data */));
        customers.add(new Customer(/* add customer data */));
        when(customerRepository.findAll()).thenReturn(customers);
 
        // Act
        List<Customer> result = customerRegisterService.getAllCustomers();
 
        // Assert
        Assertions.assertEquals(customers.size(), result.size());
        // You can add more assertions based on your requirements
    }
 
    @Test
    void testGetAllCustomers_EmptyList() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findAll()).thenReturn(customers);
 
        // Act
        List<Customer> result = customerRegisterService.getAllCustomers();
 
        // Assert
        Assertions.assertTrue(result.isEmpty());
    }
 
    @Test
    void testGetAllCustomers_NullList() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(null);
 
        // Act
        List<Customer> result = customerRegisterService.getAllCustomers();
 
        // Assert
        Assertions.assertNull(result);
    }
    @Test
    public void testUpdateCustomerDetails() {
        // Arrange
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
        addressDTO.setState("Anystate");
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
        Address existingAddress = new Address();
        existingAddress.setStreet("456 Elm St");
        existingAddress.setCity("Othertown");
        existingAddress.setState("Otherstate");
        existingAddress.setZipcode("54321");
        existingCustomer.setAddress(existingAddress);
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);
        // Act
        ResponseEntity<CustomerResponse> responseEntity = customerRegisterService.updateCustomerDetails(customerId, updatedCustomer);
        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Customer updated successfully", responseEntity.getBody().getMessage());
        assertEquals(existingCustomer, responseEntity.getBody().getData());
    }
}