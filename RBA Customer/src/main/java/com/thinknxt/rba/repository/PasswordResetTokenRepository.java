package com.thinknxt.rba.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.PasswordResetToken;
 
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
	 PasswordResetToken findByToken(String token);
 
}