package com.halasa.demoshop.rest.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdRequiresVersionValidator implements ConstraintValidator<IdRequiresVersion, IdAndVersionAware> {

    @Override
    public void initialize(IdRequiresVersion idRequiresVersion) {

    }

    @Override
    public boolean isValid(IdAndVersionAware validatedObject, ConstraintValidatorContext constraintValidatorContext) {
        return validatedObject.getId() == null && validatedObject.getEntityVersion() == null
                || validatedObject.getId() != null && validatedObject.getEntityVersion() != null;
    }
}
