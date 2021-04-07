package ru.dennis.systems.pojo_form;

import ru.dennis.systems.config.WebContext;

public class ValueNotEmptyValidator implements FormValueValidator {
    public static ValueNotEmptyValidator DEFAULT = new ValueNotEmptyValidator();
    private String field;

    @Override
    public ValidationResult validate(Object element, String field, Object value, WebContext.LocalWebContext context) {
        this.field = field;
        ValidationResult result = new ValidationResult();
        result.setResult(value != null && !String.valueOf(value).isEmpty() );
        if (!result.getResult()){
            result.setErrorMessage(context.getMessageTranslation("value.not.set." + field));
        }
        return result;
    }

    public void setField(String field) {
        this.field = field;
    }
}
