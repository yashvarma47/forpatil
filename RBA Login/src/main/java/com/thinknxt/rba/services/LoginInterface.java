package com.thinknxt.rba.services;

import com.thinknxt.rba.dto.ChangePasswordDTO;
import com.thinknxt.rba.dto.LoginDTO;
import com.thinknxt.rba.response.ChangePassResponse;
import com.thinknxt.rba.response.LoginResponse;

/**
 * Interface defining operations related to user login and registration.
 */
public interface LoginInterface {

    /**
     * Check the existence of a user based on the provided userId.
     *
     * @param user_id The unique identifier for the user.
     * @return A LoginResponse indicating the result of the existence check.
     */
    public LoginResponse userIdExist(int user_id);

    /**
     * Perform user login based on the provided login credentials.
     *
     * @param loginDTO The data transfer object containing login credentials.
     * @return A LoginResponse indicating the result of the login operation.
     */
    public LoginResponse login(LoginDTO loginDTO);

    /**
     * Delete a customer account based on the provided customerId.
     *
     * @param customerId The unique identifier for the customer.
     * @return A LoginResponse indicating the result of the customer account deletion operation.
     */
    public LoginResponse deleteCustomer(int customerId);

    /**
     * Register a new user with the provided login credentials.
     *
     * @param loginDTO The data transfer object containing user registration information.
     */
    public void registerUser(LoginDTO loginDTO);
    public ChangePassResponse changePassword(ChangePasswordDTO changePasswordDTO);
}
