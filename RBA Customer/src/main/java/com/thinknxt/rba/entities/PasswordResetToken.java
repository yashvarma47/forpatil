package com.thinknxt.rba.entities;
 
import java.sql.Timestamp;

import com.thinknxt.rba.config.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Password_reset_token")
@Generated
public class PasswordResetToken {
 
	@Id
	@Column(name = "customer_id")
    private int userId;
	@Column(name = "token_id")
    private String token;
	@Column(name = "expiry_time")
    private Timestamp expiryTime;
}