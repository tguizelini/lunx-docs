package br.com.lunx.sbootannotation.validator.conta;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContaValidator implements ConstraintValidator<Conta, String> {
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[0-9]{1,20}$");
    private boolean required;
    private String message;

    @Override
    public void initialize(Conta constraintAnnotation) {
        this.required = constraintAnnotation.required();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!isPresent(value) && !this.required) {
            return true;
        }

        return validateBacenAccount(value, context);
    }

    private boolean validateBacenAccount(String value, ConstraintValidatorContext context) {
        if (!isPresent(value)) {
            addConstraintViolation(context);
            return false;
        }

        boolean matchesPattern = ACCOUNT_PATTERN.matcher(value).matches();

        if (!matchesPattern) {
            addConstraintViolation(context);
            return false;
        }

        return true;
    }

    private boolean isPresent(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void addConstraintViolation(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(this.message).addConstraintViolation();
    }
}
