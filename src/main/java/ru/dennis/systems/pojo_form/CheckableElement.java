package ru.dennis.systems.pojo_form;

import lombok.Data;

@Data
public class CheckableElement {
    private boolean isCheckable = false;
    private String value;
    private boolean isChecked;

    public static CheckableElement from(Checkable checked) {
         CheckableElement element = new CheckableElement();
         element.setCheckable(checked.isCheckElement());
         element.setValue(checked.value());
         element.setChecked(checked.checked());
         return element;
    }
}
