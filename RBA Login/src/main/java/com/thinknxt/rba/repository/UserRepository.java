package com.thinknxt.rba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.LoginDetails;

@Generated
@Repository
public interface UserRepository extends JpaRepository<LoginDetails,Integer> {
	LoginDetails findByUserId(String username);
}
