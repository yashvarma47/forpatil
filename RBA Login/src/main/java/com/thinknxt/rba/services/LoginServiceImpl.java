package com.thinknxt.rba.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinknxt.rba.dto.ChangePasswordDTO;
import com.thinknxt.rba.dto.LoginDTO;
import com.thinknxt.rba.dto.LoginUserInfoDTO;
import com.thinknxt.rba.entities.LoginDetails;
import com.thinknxt.rba.repository.LoginRepository;
import com.thinknxt.rba.response.ChangePassResponse;
import com.thinknxt.rba.response.LoginResponse;
import com.thinknxt.rba.utils.Status;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the LoginInterface providing user login and registration
 * services.
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginInterface {

	@Autowired
	private LoginRepository loginRepository;

	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 * Check the existence of a user based on the provided userId.
	 *
	 * @param user_id The unique identifier for the user.
	 * @return A LoginResponse indicating the result of the existence check.
	 */
	public LoginResponse userIdExist(int user_id) {
		log.info("Inside userIdExist function of LoginServiceImpl class of RBA login ");
		LoginResponse loginResponse = new LoginResponse();
		log.info("Inside userIdExist function of LoginServiceImpl class of RBA login");
		LoginDetails loginDetails = loginRepository.findByuserId(user_id);
		if (loginDetails != null && loginDetails.getUserId() == user_id) {
			log.info("If part inside userIdExist function of LoginServiceImpl class of RBA login ");
			if (StringUtils.equals(loginDetails.getAccount_status(), Status.ACTIVE.toString())) {
				loginResponse.setMessage("User: " + user_id + " is a valid user");
				loginResponse.setStatus(200);
			} else if (StringUtils.equals(loginDetails.getAccount_status(), Status.DELETED.toString())) {
				loginResponse.setMessage("Kindly contact Branch");
				loginResponse.setStatus(404);
			} else if (StringUtils.equals(loginDetails.getAccount_status(), Status.INACTIVE.toString())) {
				loginResponse.setMessage("User: " + user_id + " is " + loginDetails.getAccount_status());
				loginResponse.setStatus(404);
			}
			log.info("End of If part inside userIdExist function of LoginServiceImpl class of RBA login ");
		} else {
			loginResponse.setMessage("User: " + user_id + " is not registered");
			loginResponse.setStatus(404);
			log.info("Else part inside userIdExist function of LoginServiceImpl class of RBA login ");
		}
		log.info("End of userIdExist function of LoginServiceImpl class of RBA login ");
		return loginResponse;
	}

	/**
	 * Perform user login based on the provided login credentials.
	 *
	 * @param loginDTO The data transfer object containing login credentials.
	 * @return A LoginResponse indicating the result of the login operation.
	 */
	public LoginResponse login(LoginDTO loginDTO) {
		log.info("Inside login function of LoginServiceImpl class of RBA login ");

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		LoginResponse loginResponse = new LoginResponse();
		LoginUserInfoDTO loginUserInfoDTO = new LoginUserInfoDTO();

		int userStatus = userIdExist(loginDTO.getUserId()).getStatus();
		if (userStatus == 200) {
			log.info("If part inside of login function of LoginServiceImpl class of RBA login ");
			LoginDetails login = loginRepository.findByuserId(loginDTO.getUserId());

			if (login != null && passwordEncoder.matches(loginDTO.getPassword(), login.getPassword())) {
				loginResponse.setMessage("User: " + loginDTO.getUserId() + " has successfully logged in!!!!");
				loginResponse.setStatus(200);
				loginUserInfoDTO.setRole(login.getRole());
				loginUserInfoDTO.setAccount_status(login.getAccount_status());
				loginResponse.setData(loginUserInfoDTO);
			} else {
				loginResponse
						.setMessage("Incorrect Credentials!!! Please enter valid credentials....Please try again!!!");
				loginResponse.setStatus(401);
			}
			log.info("End of If part inside of login function of LoginServiceImpl class of RBA login ");
		} else {
			loginResponse.setMessage("User: " + loginDTO.getUserId() + " is not registered");
			loginResponse.setStatus(404);
		}
		log.info("End of login function of LoginServiceImpl class of RBA login ");
		return loginResponse;
	}

	/**
	 * Delete a customer account based on the provided customerId.
	 *
	 * @param customerId The unique identifier for the customer.
	 * @return A LoginResponse indicating the result of the customer account
	 *         deletion operation.
	 */
	@Override
	public LoginResponse deleteCustomer(int customerId) {
		log.info("Inside deleteCustomer function of LoginServiceImpl class of RBA login ");
		LoginResponse loginResponse = new LoginResponse();
		LoginDetails loginDetails = loginRepository.findByuserId(customerId);
		loginDetails.setAccount_status(Status.DELETED.toString());
		loginRepository.save(loginDetails);
		loginResponse.setMessage("Account Deleted Successfully!!");
		loginResponse.setStatus(200);
		log.info("End of deleteCustomer function of LoginServiceImpl class of RBA login ");
		return loginResponse;
	}

	/**
	 * Register a new user with the provided login credentials.
	 *
	 * @param loginDTO The data transfer object containing user registration
	 *                 information.
	 */
	@Override
	public void registerUser(LoginDTO loginDTO) {
		log.info("Inside registerUser function of LoginServiceImpl class of RBA login ");
		LoginDetails loginDetails = new LoginDetails();
		loginDetails.setUserId(loginDTO.getUserId());
		loginDetails.setPassword(loginDTO.getPassword());
		loginDetails.setRole("CUSTOMER");
		loginDetails.setAccount_status(Status.ACTIVE.toString());

		loginRepository.save(loginDetails);
	}

	/**
	 * Change password after validating with your old password.
	 *
	 * @param ChangePasswordDTO The data transfer object containing
	 *                          customerId,oldpassword.newPassword.
	 */

	ChangePassResponse response = new ChangePassResponse();

	@Override
	@Transactional
	public ChangePassResponse changePassword(ChangePasswordDTO changePasswordDTO) {
		log.info("Inside the service of ChangePassword with the changePasswordDTO object ");

		LoginDetails loginDetails = loginRepository.findByuserId(changePasswordDTO.getCustomerid());
		/*
		 * checking that loginDetails is null or not with the given customerId.
		 *
		 * if it is null it will go in else part that customerId Not found if it is not
		 * null it will go inside
		 */
		if (loginDetails != null) {

//			Checking the oldPassword given and Database password function


			if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), loginDetails.getPassword())) {
				log.info("Inside the service of ChangePassword and Given password doesnot match with database ");

				;
				response.setStatus(400);
				response.setMessage("Old password doesnot matches");
				log.info("Ending service with message Old password doesnot matches");
				log.info("Old passs checked");
				return response;
			}
//			New password and existing password cannot be same
			 
			else if(passwordEncoder.matches(changePasswordDTO.getNewPassword(), loginDetails.getPassword())) {
				log.info("Inside the service of ChangePassword and New password is same as Existing password ");
				response.setStatus(401);
				response.setMessage("Given Password and existing password can't be same");
				log.info("Ending service with message new password cannot be same");
				return response;
			}
//			oldPassword and database password matched and it is saving the new password
			else {
				log.info(
						"Inside the service of ChangePassword and Given password matches with database and password chnaged");
				loginDetails.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
				loginRepository.save(loginDetails);

				response.setStatus(200);
				response.setMessage("Password updated successfully");
				log.info("Ending service with message Password chnaged successfully");

				return response;
			}

		}
//		The given UserId doesn't found In the database then it is going in else part

		else {
			log.info("Inside the service of ChangePassword and UserId not found in the database");

			response.setStatus(404);
			response.setMessage("User Not found with the CustomerId: " + changePasswordDTO.getCustomerid());
			log.info("Ending service with message UserId not found in the database");

			return response;
		}
	}
	
	// Sidheshwar
	public LoginDetails fetchLoginDetails(int userId) {
		LoginDetails loginDetails = loginRepository.findByuserId(userId);
		if(loginDetails != null) {
			return loginDetails;
		}
		
		else {
		  throw new RuntimeException("User doesnt exist!!!");
		}
	}
	
	/**
	 * @author nirajku
	 *Reseting the password on the basis of customerId condition like,
	 *Existing and newPassword can't be same 
	 */
	public int resetPassword(int customerId, String newPassword) {
		log.info("Inside the resetPassword function og login service ");
//		Finding details with given customer Id 
	    LoginDetails loginDetails = loginRepository.findByuserId(customerId);
	    if (loginDetails != null) {
//     		Logging details is found 	
	    	log.info("Inside the if statement and details found with customer Id "+customerId);
	        if (passwordEncoder.matches(newPassword, loginDetails.getPassword())) {
//	     		Existing password and new password is same  	
	            log.info("Inside the service of resetPassword and New password is same as Existing password");
	            log.info("Ending service with message new password cannot be same and status code 401");
	            return 400;
	        } else {
	        	 log.info("Inside the service of resetPassword and saving the new password");
//	     		Saving the new Password       	
	            loginDetails.setPassword(passwordEncoder.encode(newPassword));
	            loginRepository.save(loginDetails);
	            log.info("Ending service with status code 200");
	            return 200;
	        }
	    } else {
//	    	     	Customer Not Found with the given Id  
	    	  log.info("Ending service with status code 404 as No customer found with given customerId");
	        return 404;
	    }
	}
	
}
