package ru.dennis.systems.controller.forms;

import ru.dennis.systems.pojo_form.ValidationContext;

public interface OnSaveListener<T, FORM> {
    String onSave(boolean ok, T object, FORM form, Exception e);

    String validationErrors(boolean b, Object o, FORM form, ValidationContext context);
}
