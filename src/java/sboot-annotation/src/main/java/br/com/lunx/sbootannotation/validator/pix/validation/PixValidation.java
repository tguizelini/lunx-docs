package br.com.lunx.sbootannotation.validator.pix.validation;

import java.util.regex.Pattern;

import jakarta.validation.ValidationException;

public class PixValidation {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern BACEN_PATTERN = Pattern.compile("^\\d{20}$");

    public static void validatePixKey(String value, PixType type) {
        switch (type) {
            case BACEN:
                validateBacen(value);
                break;
            case EMAIL:
                validateEmail(value);
                break;
            case CPF:
                validateCpf(value);
                break;
            default:
                validateBacen(value);
                break;
        }
    }

    private static void validateBacen(String value) {
        if (value.length() != 20) {
            throw new ValidationException("Chave PIX Bacen deve conter 20 dígitos numéricos");
        }

        String numericOnly = value.replaceAll("[^0-9]", "");

        if (!BACEN_PATTERN.matcher(numericOnly).matches()) {
            throw new ValidationException("Chave PIX Bacen inválida");
        }
    }

    private static void validateEmail(String value) {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new ValidationException("Chave PIX do tipo EMAIL inválida");
        }
    }

    private static void validateCpf(String value) {
        String numericOnly = value.replaceAll("[^0-9]", "");
        if (!CPF_PATTERN.matcher(numericOnly).matches()) {
            throw new ValidationException("Chave PIX do tipo CPF deve conter 11 dígitos");
        }
        if (!isValidCPF(numericOnly)) {
            throw new ValidationException("Chave PIX do tipo CPF inválida");
        }
    }

    private static boolean isValidCPF(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        int[] digits = cpf.chars().map(Character::getNumericValue).toArray();

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;
        if (firstDigit != digits[9]) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;
        return secondDigit == digits[10];
    }
} 