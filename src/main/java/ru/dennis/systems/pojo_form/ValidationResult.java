package ru.dennis.systems.pojo_form;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ValidationResult {
    private Boolean result;
    private String errorMessage;
    public static final List<ValidationResult> OK = ok();
    public static final ValidationResult PASSED = passed();

    private static ValidationResult passed() {
        ValidationResult result = new ValidationResult();
        result.setResult(true);
        return result;
    }

    public static ValidationResult fail(String message) {
        ValidationResult result = new ValidationResult();
        result.setResult(false);
        result.setErrorMessage(message);
        return result;
    }



    private static List<ValidationResult> ok() {
        ValidationResult result = new ValidationResult();
        result.setResult(true);
        return Collections.singletonList(result);
    }
}
