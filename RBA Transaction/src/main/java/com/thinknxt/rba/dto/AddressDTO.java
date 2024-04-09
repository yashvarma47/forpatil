package com.thinknxt.rba.dto;
import java.sql.Date;

import com.thinknxt.rba.config.Generated;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    
    @Size(min = 6, max = 6, message = "Zipcode should be of 6 Digits only!!!")
    private String zipCode;
}