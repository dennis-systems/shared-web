package ru.dennis.systems.pojo_form;

import ru.dennis.systems.config.WebContext;

public interface FormValueValidator <FIELD_CLASS, VALUE_CLASS> {
    public ValidationResult validate(FIELD_CLASS element, String field, VALUE_CLASS value, WebContext.LocalWebContext context);

}
