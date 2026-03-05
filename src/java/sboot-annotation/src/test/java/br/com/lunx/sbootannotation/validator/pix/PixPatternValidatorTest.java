package br.com.lunx.sbootannotation.validator.pix;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.lunx.sbootannotation.validator.pix.PixPattern;
import br.com.lunx.sbootannotation.validator.pix.PixPatternValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import jakarta.validation.ConstraintValidatorContext;

class PixPatternValidatorTest {
    private PixPatternValidator validator;
    private ConstraintValidatorContext context;
    private PixPattern pixPattern;

    @BeforeEach
    void setUp() {
        validator = new PixPatternValidator();
        context = mock(ConstraintValidatorContext.class);
        pixPattern = mock(PixPattern.class);
        when(pixPattern.type()).thenReturn(PixType.BACEN);
        validator.initialize(pixPattern);
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
    void shouldReturnFalseWhenValueIsNull() {
        assertFalse(validator.isValid(null, context));
    }
} 