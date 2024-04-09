package com.thinknxt.rba.response;

import java.util.List;

import com.thinknxt.rba.config.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class ErrorResponse {
    private int status;
    private String error;
    private List<ErrorDetail> details;
}
