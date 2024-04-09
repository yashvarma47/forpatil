package com.thinknxt.rba.dto;
 
import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class AddBenificiaryDTO {
    private int customerId;
    private long accountNumber;
    private String name;
    private String email;
}