package com.halasa.demoshop.rest.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class validation annotation which requires an IdAndVersionAware object to contain either both values (id and entityVersion) or none.
 * Request DTOs meant for creating new entities should have no id and entityVersion values. Updating request should have them both.
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { IdRequiresVersionValidator.class })
@Documented
public @interface IdRequiresVersion {
    String message() default "The object must contain either both id and entityVersion values or none of them";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
