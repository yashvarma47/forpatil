package com.thinknxt.rba.entities;

import com.thinknxt.rba.config.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "login_details")
@Generated
public class LoginDetails {

    @Id
    @Column(name = "user_id", length = 8)
    private int userId;
    
    @Column(name = "password", length = 15)
    private String password;
    
    @Column(name = "role", length = 45)
    private String role;
    
    @Column(name = "account_status", length = 45)
    private String account_status;
    
}
