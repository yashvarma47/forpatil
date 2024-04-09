package com.thinknxt.rba.config;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR,ElementType.PACKAGE})
public @interface Generated {
}
