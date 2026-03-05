package br.com.lunx.sbootannotation.validator.pix;

import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import br.com.lunx.sbootannotation.validator.pix.validation.PixValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;

public class PixPatternValidator implements ConstraintValidator<PixPattern, String> {
    private PixType type;

    @Override
    public void initialize(PixPattern constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if (value == null || value.trim().isEmpty()) {
                throw new ValidationException("Chave PIX não informada");
            }

            PixValidation.validatePixKey(value, type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 