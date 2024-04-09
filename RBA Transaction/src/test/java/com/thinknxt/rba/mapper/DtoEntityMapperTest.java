package com.thinknxt.rba.mapper;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.thinknxt.rba.dto.TransferRequestDTO;
import com.thinknxt.rba.dto.Accounts;
import com.thinknxt.rba.entities.CurrentsTransactionEntity;
import com.thinknxt.rba.entities.SavingsTransactionEntity;
import com.thinknxt.rba.entities.TransactionEntity;
//import com.thinknxt.rba.response.AccountDetailsResponse;
import com.thinknxt.rba.response.TransactionResponse;

class DtoEntityMapperTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DtoEntityMapper dtoEntityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAccountToAccountTransactionDTOtoEntitySavToSav() {
    	
        mockRestTemplate();

        TransactionResponse transactionResponse = createTransactionResponse("SAVINGS", 1500.0);
        TransferRequestDTO transferRequestDTO = createTransferRequestDTO("SAVINGS", 500.0);
        double senderTotalBalance = 1500.0;
        int senderCustomerId = 123;
        String transactionId = "txn123";
        String senderAccountType = "SAVINGS";
        String benefAccountType = "SAVINGS";

        Map<String, Object> result = dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
                transactionResponse, transferRequestDTO, senderTotalBalance, senderCustomerId,
                transactionId, senderAccountType, benefAccountType);

        assertNotNull(result);
        assertEquals("savToSav", result.get("transferRequestType"));

        SavingsTransactionEntity beneficiarySavingsEntity =
                (SavingsTransactionEntity) result.get("savingsTransactionEntityForBeneficiaryAccount");
        assertNotNull(beneficiarySavingsEntity);
        
        assertEquals(1500.0, beneficiarySavingsEntity.getRemainingBalance());
        assertEquals(500.0, beneficiarySavingsEntity.getTransactionAmount());

        SavingsTransactionEntity senderSavingsEntity =
                (SavingsTransactionEntity) result.get("savingsTransactionEntityForSenderAccount");
        assertNotNull(senderSavingsEntity);
        assertEquals(1000.0, senderSavingsEntity.getRemainingBalance());
        assertEquals(500.0, senderSavingsEntity.getTransactionAmount());

        TransactionEntity transactionEntity = (TransactionEntity) result.get("transactionEntity");
        assertNotNull(transactionEntity);
        assertEquals("SUCCESS", transactionEntity.getTransactionStatus());
        assertEquals("NET BANKING", transactionEntity.getTransactionMode());
    }

    
    @Test
    void testAccountToAccountTransactionDTOtoEntitySavToCur() {
        mockRestTemplate();

        TransactionResponse transactionResponse = createTransactionResponse("SAVINGS", 1500.0);
        TransferRequestDTO transferRequestDTO = createTransferRequestDTO("SAVINGS", 500.0);
        double senderTotalBalance = 1500.0;
        int senderCustomerId = 123;
        String transactionId = "txn123";
        String senderAccountType = "SAVINGS";
        String benefAccountType = "CURRENT";

        Map<String, Object> result = dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
                transactionResponse, transferRequestDTO, senderTotalBalance, senderCustomerId,
                transactionId, senderAccountType, benefAccountType);

        assertNotNull(result);
        assertEquals("savToCur", result.get("transferRequestType"));

        CurrentsTransactionEntity beneficiaryCurrentsEntity =
                 (CurrentsTransactionEntity) result.get("currentsTransactionEntityForBeneficiaryAccount");

        assertNotNull(beneficiaryCurrentsEntity);
        
        assertEquals(1500.0, beneficiaryCurrentsEntity.getRemainingBalance());
        assertEquals(500.0, beneficiaryCurrentsEntity.getTransactionAmount());

        SavingsTransactionEntity senderSavingsEntity =
                (SavingsTransactionEntity) result.get("savingsTransactionEntityForSenderAccount");
        assertNotNull(senderSavingsEntity);
       
        assertEquals(1000.0, senderSavingsEntity.getRemainingBalance());
        assertEquals(500.0, senderSavingsEntity.getTransactionAmount());

        TransactionEntity transactionEntity = (TransactionEntity) result.get("transactionEntity");
        assertNotNull(transactionEntity);
        assertEquals("SUCCESS", transactionEntity.getTransactionStatus());
        assertEquals("NET BANKING", transactionEntity.getTransactionMode());
    }

    @Test
    void testAccountToAccountTransactionDTOtoEntityCurToCur() {
    	
        mockRestTemplate();

        TransactionResponse transactionResponse = createTransactionResponse("CURRENT", 1500.0);
        TransferRequestDTO transferRequestDTO = createTransferRequestDTO("CURRENT", 500.0);
        double senderTotalBalance = 1500.0;
        int senderCustomerId = 123;
        String transactionId = "txn123";
        String senderAccountType = "CURRENT";
        String benefAccountType = "CURRENT";

        

        Map<String, Object> result = dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
                transactionResponse, transferRequestDTO, senderTotalBalance, senderCustomerId,
                transactionId, senderAccountType, benefAccountType);

        assertNotNull(result);
        assertEquals("curToCur", result.get("transferRequestType"));

        
        CurrentsTransactionEntity beneficiaryCurrentsEntity =
                (CurrentsTransactionEntity) result.get("currentsTransactionEntityForBeneficiaryAccount");
        assertNotNull(beneficiaryCurrentsEntity);
        
        assertEquals(1500.0, beneficiaryCurrentsEntity.getRemainingBalance());
        assertEquals(500.0, beneficiaryCurrentsEntity.getTransactionAmount());

        CurrentsTransactionEntity senderCurrentsEntity =
                (CurrentsTransactionEntity) result.get("currentsTransactionEntityForSenderAccount");
        assertNotNull(senderCurrentsEntity);
        assertEquals(1000.0, senderCurrentsEntity.getRemainingBalance());
        assertEquals(500.0, senderCurrentsEntity.getTransactionAmount());

        TransactionEntity transactionEntity = (TransactionEntity) result.get("transactionEntity");
        assertNotNull(transactionEntity);
        assertEquals("SUCCESS", transactionEntity.getTransactionStatus());
        assertEquals("NET BANKING", transactionEntity.getTransactionMode());
    }

    
    @Test
    void testAccountToAccountTransactionDTOtoEntityCurToSav() {
        mockRestTemplate();

        TransactionResponse transactionResponse = createTransactionResponse("SAVINGS", 1500.0);
        TransferRequestDTO transferRequestDTO = createTransferRequestDTO("CURRENT", 500.0);
        double senderTotalBalance = 1500.0;
        int senderCustomerId = 123;
        String transactionId = "txn123";
        String senderAccountType = "CURRENT";
        String benefAccountType = "SAVINGS";

        Map<String, Object> result = dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
                transactionResponse, transferRequestDTO, senderTotalBalance, senderCustomerId,
                transactionId, senderAccountType, benefAccountType);

        assertNotNull(result);
        assertEquals("curToSav", result.get("transferRequestType"));

        SavingsTransactionEntity beneficiarySavingsEntity =
                 (SavingsTransactionEntity) result.get("savingsTransactionEntityForBeneficiaryAccount");

        assertNotNull(beneficiarySavingsEntity);
        
        assertEquals(1500.0, beneficiarySavingsEntity.getRemainingBalance());
        assertEquals(500.0, beneficiarySavingsEntity.getTransactionAmount());

        CurrentsTransactionEntity senderCurrentsEntity =
                (CurrentsTransactionEntity) result.get("currentsTransactionEntityForSenderAccount");
        assertNotNull(senderCurrentsEntity);
       
        assertEquals(1000.0, senderCurrentsEntity.getRemainingBalance());
        assertEquals(500.0, senderCurrentsEntity.getTransactionAmount());

        TransactionEntity transactionEntity = (TransactionEntity) result.get("transactionEntity");
        assertNotNull(transactionEntity);
        assertEquals("SUCCESS", transactionEntity.getTransactionStatus());
        assertEquals("NET BANKING", transactionEntity.getTransactionMode());
    }

    
    
    
    
    
    
    
    
    
      @Test
    void testAccountToAccountTransactionDTOtoEntityInvalidAccountType() {
        mockRestTemplate();

        TransactionResponse transactionResponse = createTransactionResponse("INVALID_TYPE", 1000.0);
        TransferRequestDTO transferRequestDTO = createTransferRequestDTO("SAVINGS", 500.0);
        double senderTotalBalance = 1500.0;
        int senderCustomerId = 123;
        String transactionId = "txn123";
        String senderAccountType = "SAVINGS";
        String benefAccountType = "INVALID_TYPE";

        Map<String, Object> result = dtoEntityMapper.accountToAccountTransactionDTOtoEntity(
                transactionResponse, transferRequestDTO, senderTotalBalance, senderCustomerId,
                transactionId, senderAccountType, benefAccountType);

        assertNotNull(result);
        assertNull(result.get("transferRequestType"));
        assertNull(result.get("savingsTransactionEntityForBeneficiaryAccount"));
        assertNull(result.get("savingsTransactionEntityForSenderAccount"));
        assertNull(result.get("transactionEntity"));
    }

    private TransactionResponse createTransactionResponse(String accountType, double totalBalance) {
        TransactionResponse response = new TransactionResponse();
        Accounts sampleAccountsData = new Accounts();
        sampleAccountsData.setAccountnumber(9876543210L);
        sampleAccountsData.setTotalbalance(1000.0);
        sampleAccountsData.setAccountstatus("ACTIVE");
        sampleAccountsData.setCurrency("INR");
        sampleAccountsData.setOverdraft("NO");
        sampleAccountsData.setAccounttype("SAVINGS");
        sampleAccountsData.setCustomerid(123);
        
        response.setData(sampleAccountsData);
        return response;
    }

    private TransferRequestDTO createTransferRequestDTO(String accountType, double transferAmount) {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setTransferAmount(transferAmount);
        return transferRequestDTO;
    }
    

    @SuppressWarnings("unchecked")
	private void mockRestTemplate() {
        when(restTemplate.getForEntity(any(String.class), any(Class.class)))
                .thenReturn(ResponseEntity.ok(new Accounts()));
        when(restTemplate.postForEntity(any(String.class), any(MultiValueMap.class), any(Class.class)))
                .thenReturn(ResponseEntity.ok().build());
    }
    
    
    
}
