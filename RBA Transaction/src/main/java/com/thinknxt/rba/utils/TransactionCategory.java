package com.thinknxt.rba.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinknxt.rba.config.Generated;

@Generated
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TransactionCategory {
	food,
    fuel,
    travel,
    bills,
    entertainment,
    loan,
    recharge,
    shopping,
    miscellaneous
}
