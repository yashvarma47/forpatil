package com.thinknxt.rba.dto;
import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class ChangePasswordDTO {
	private int customerid;
	private String oldPassword;
    private String newPassword;
}