package br.com.lunx.sbootannotation.validator.conta;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContaValidatorTest {

    private ContaValidator validator;

    @Mock
    private Conta constraintAnnotation;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private static final String MESSAGE_ERROR = "A conta deve possuir entre 6 e 20 dígitos numéricos";

    @BeforeEach
    void setUp() {
        validator = new ContaValidator();
        when(constraintAnnotation.required()).thenReturn(true);
        when(constraintAnnotation.message()).thenReturn(MESSAGE_ERROR);
        validator.initialize(constraintAnnotation);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void shouldValidateValidAccount() {
        String valid = "12345678901234567890";
        assertTrue(validator.isValid(valid, context));
    }

    @Test
    void shouldValidateValidAccountWithSpaces() {
        String valid = "1234 5678 9012 3456 7890";
        assertFalse(validator.isValid(valid, context));
    }

    @Test
    void shouldValidateValidAccountWithSpecialCharacters() {
        String valid = "1234-5678-9012-3456-7890";
        assertFalse(validator.isValid(valid, context));
    }

    @Test
    void shouldValidateMinimumLengthAccount() {
        String valid = "123456"; // 6 digitos
        assertTrue(validator.isValid(valid, context));
    }

    @Test
    void shouldValidateMaximumLengthAccount() {
        String valid = "12345678901234567890"; // 20 digitos
        assertTrue(validator.isValid(valid, context));
    }

    @Test
    void shouldRejectAccountWithLessThanMinimumDigits() {
        String invalid = "12345"; // 5 digitos
        assertTrue(validator.isValid(invalid, context));
    }

    @Test
    void shouldRejectAccountWithMoreThanMaximumDigits() {
        String invalid = "123456789012345678901"; // 21 digitos
        assertFalse(validator.isValid(invalid, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(MESSAGE_ERROR);
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectAccountWithLetters() {
        String invalid = "1234567890123456789A";
        assertFalse(validator.isValid(invalid, context));
    }

    @Test
    void shouldAcceptNullWhenNotRequired() {
        when(constraintAnnotation.required()).thenReturn(false);
        validator.initialize(constraintAnnotation);
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldRejectNullWhenRequired() {
        assertFalse(validator.isValid(null, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(MESSAGE_ERROR);
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectEmptyStringWhenRequired() {
        assertFalse(validator.isValid("", context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(MESSAGE_ERROR);
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectBlankStringWhenRequired() {
        assertFalse(validator.isValid("   ", context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(MESSAGE_ERROR);
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectAccountWithMixedLengthAfterCleaning() {
        String invalid = "123-45"; // 5 digitos após remover characteres nao numericos
        assertFalse(validator.isValid(invalid, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(MESSAGE_ERROR);
        verify(builder).addConstraintViolation();
    }
}
