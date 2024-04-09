package com.thinknxt.rba.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.Customer;

/**
 * Repository interface for accessing customer data in the database.
 */
@Repository
@Generated
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     *  Query to find a customer by customerId, email, and phoneNumber.
     *
     * @param customerId The unique identifier for the customer.
     * @param email The email address of the customer.
     * @param phoneNumber The phone number of the customer.
     * @return The customer entity matching the specified criteria.
     */
    @Query(value = "SELECT * FROM customers c WHERE c.customer_id = ?1 and c.email = ?2 and c.phone_number = ?3", nativeQuery = true)
    Customer findByCustomerIdAndEmailAndPhoneNumber(int customerId, String email, String phoneNumber);

    /**
     * Find a customer by PAN number, Aadhar number, date of birth, and phone number.
     *
     * @param pan The PAN number of the customer.
     * @param aadhar The Aadhar number of the customer.
     * @param dateOfBirth The date of birth of the customer.
     * @param phoneNumber The phone number of the customer.
     * @return The customer entity matching the specified criteria.
     */
    Customer findByPanNumberAndAadharNumberAndDateOfBirthAndPhoneNumber(String pan, String aadhar, Date dateOfBirth,
            String phoneNumber);

    /**
     * Check if a customer exists by PAN number.
     *
     * @param pan The PAN number to check.
     * @return True if a customer with the specified PAN number exists; otherwise, false.
     */
    boolean existsByPanNumber(String pan);

    /**
     * Check if a customer exists by Aadhar number.
     *
     * @param aadhar The Aadhar number to check.
     * @return True if a customer with the specified Aadhar number exists; otherwise, false.
     */
    boolean existsByAadharNumber(String aadhar);

    /**
     * Find a customer by customerId.
     *
     * @param customerId The unique identifier for the customer.
     * @return The customer entity matching the specified customerId.
     */
    Customer findByCustomerId(int customerId);

	Customer findByCustomerIdAndPanNumberAndDateOfBirthAndPhoneNumber(int userId, String pan, Date dateOfBirth,
			String phoneNumber);

	/**
	     * Check if a customer exists by Customer Id.
	     *
	     * @param customerId The customer id to check.
	     * @return True if a customer with the specified customerId exists; otherwise, false.
	     */
	    boolean existsByCustomerId(int customerId);
}

