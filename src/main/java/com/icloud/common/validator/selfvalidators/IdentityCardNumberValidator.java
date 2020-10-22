package com.icloud.common.validator.selfvalidators;

import com.icloud.common.util.IdCardValidatorUtils;
import com.icloud.common.validator.customizeannotation.IdentityCardNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdentityCardNumberValidator implements ConstraintValidator<IdentityCardNumber, Object> {

    @Override
    public void initialize(IdentityCardNumber identityCardNumber) {
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return new IdCardValidatorUtils(o.toString()).validate();
    }
}