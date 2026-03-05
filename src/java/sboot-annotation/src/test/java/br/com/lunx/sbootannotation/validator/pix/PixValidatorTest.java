package br.com.lunx.sbootannotation.validator.pix;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.lunx.sbootannotation.validator.pix.Pix;
import br.com.lunx.sbootannotation.validator.pix.PixValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import jakarta.validation.ConstraintValidatorContext;

class PixValidatorTest {
    private PixValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;
    private Pix pix;

    @BeforeEach
    void setUp() {
        validator = new PixValidator();
        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
        pix = mock(Pix.class);
        when(pix.type()).thenReturn(PixType.BACEN);
        validator.initialize(pix);
    }

    @Test
    void shouldReturnTrueWhenValidationPasses() {
        assertTrue(validator.isValid("12345678901234567890", context));
    }

    @Test
    void shouldReturnFalseWhenValidationFails() {
        assertFalse(validator.isValid("invalid", context));
    }

    @Test
    void shouldReturnTrueWhenValueIsNull() {
        assertTrue(validator.isValid(null, context));
    }
}
