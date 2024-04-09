package com.thinknxt.rba.dto;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class EmailRequest {
    private String to;
    private String subject;
    private String body;

}