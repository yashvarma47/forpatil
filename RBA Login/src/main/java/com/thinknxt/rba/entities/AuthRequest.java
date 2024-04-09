package com.thinknxt.rba.entities;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Generated
@NoArgsConstructor
public class AuthRequest {

    private String userId;
    private String password;
}
