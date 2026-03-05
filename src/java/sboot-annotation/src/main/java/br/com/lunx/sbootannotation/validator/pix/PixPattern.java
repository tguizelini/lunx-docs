package br.com.lunx.sbootannotation.validator.pix;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PixPatternValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PixPattern {
    String message() default "Chave PIX inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    PixType type() default PixType.BACEN;
} 