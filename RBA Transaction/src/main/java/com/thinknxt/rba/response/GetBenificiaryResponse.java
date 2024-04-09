

package com.thinknxt.rba.response;

import java.util.List; // Import List interface

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.BenificiaryAccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class GetBenificiaryResponse {
	
    private int status;
    private String message;
    private List<BenificiaryAccount> data;
}