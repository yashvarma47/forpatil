package com.thinknxt.rba.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.LoginDetails;

@Repository
@Generated
public interface LoginRepository extends JpaRepository<LoginDetails, Integer> {
	public LoginDetails findByuserId(int user_id);
		
}