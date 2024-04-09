package com.thinknxt.rba.dto;
import java.sql.Date;

import com.thinknxt.rba.config.Generated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class CustomerDTO {
    private String firstName;
    private String lastName;
    private AddressDTO addressDTO;
    
    @NotNull(message = "Date cannot be null")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    
    private String gender;
    
    @Email(message = "Please enter valid email address!!!")
    private String email;
    
    private String phoneNumber;
    
    @Size(min = 10, max = 10, message = "PAN number should be of 10 characters only!!!")
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}")
    private String panNumber;
    
    @Size(min=12, max = 12, message = "AADHAR number should be of 12 characters only!!!")
    private String aadharNumber;
}