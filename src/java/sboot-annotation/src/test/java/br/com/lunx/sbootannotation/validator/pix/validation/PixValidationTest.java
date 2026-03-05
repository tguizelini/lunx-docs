package br.com.lunx.sbootannotation.validator.pix.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.lunx.sbootannotation.validator.pix.validation.PixType;
import br.com.lunx.sbootannotation.validator.pix.validation.PixValidation;
import org.junit.jupiter.api.Test;

class PixValidationTest {
    private final PixValidation pixValidation = new PixValidation();

    @Test
    void shouldValidateValidBacenKey() {
        assertDoesNotThrow(() -> pixValidation.validatePixKey("12345678901234567890", PixType.BACEN));
    }

    @Test
    void shouldThrowForInvalidBacenKey() {
        assertThrows(Exception.class, () -> pixValidation.validatePixKey("invalid", PixType.BACEN));
    }

    @Test
    void shouldValidateValidEmailKey() {
        assertDoesNotThrow(() -> pixValidation.validatePixKey("test@email.com", PixType.EMAIL));
    }

    @Test
    void shouldThrowForInvalidEmailKey() {
        assertThrows(Exception.class, () -> pixValidation.validatePixKey("invalid-email", PixType.EMAIL));
    }

    @Test
    void shouldValidateValidCpfKey() {
        assertDoesNotThrow(() -> pixValidation.validatePixKey("39397569899", PixType.CPF));
    }

    @Test
    void shouldThrowForInvalidCpfKey() {
        assertThrows(Exception.class, () -> pixValidation.validatePixKey("12345678900", PixType.CPF));
    }
} 