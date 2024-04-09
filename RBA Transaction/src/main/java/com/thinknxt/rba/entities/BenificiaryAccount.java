package com.thinknxt.rba.entities;
 
import com.thinknxt.rba.config.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Generated
@Table(name = "benificiary_accounts")
public class BenificiaryAccount {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ben_account_number")
    private long benaccountnumber;
    @Column(name = "customer_id")
    private int customerid;
 
    @Column(name = "ben_name")
    private String ben_name;
 
    @Column(name = "ben_email")
    private String ben_email;
 

}