package com.thinknxt.rba.dto;
import java.sql.Date;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class Accounts {
    private long accountnumber;
    private int customerid;
    private String accounttype;
    private String accountstatus;
    private String currency;
    private String overdraft;
    private Date creationdate;
    private double totalbalance;
}