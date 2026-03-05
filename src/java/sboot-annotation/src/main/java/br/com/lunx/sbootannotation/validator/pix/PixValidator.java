package br.com.lunx.sbootannotation.validator.pix;

import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import br.com.lunx.sbootannotation.validator.pix.validation.PixValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PixValidator implements ConstraintValidator<Pix, String> {
    private PixType type;

    @Override
    public void initialize(Pix constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // quem controlla se o valor é nullable é o @NotNull no campo da model
        if (value == null) return true;

        try {
            PixValidation.validatePixKey(value, type);
            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                   .addConstraintViolation();
            return false;
        }
    }
} 