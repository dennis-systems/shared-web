package ru.dennis.systems.pojo_form;

import ru.dennis.systems.config.WebContext;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.dennis.systems.pojo_view.DEFAULT_TYPES.DEFAULT_EMPTY_VALUE;
import static ru.dennis.systems.pojo_view.DEFAULT_TYPES.DEFAULT_TYPE;

@Data
public class FormElement {

    public static FormElement createDefault(String field, WebContext.LocalWebContext context) {
        FormElement element = new FormElement();
        element.setAutocomplete("on");
        element.setPlaceHolder("");
        element.setChecked(new CheckableElement());
        element.setDataConverter(null);
        element.setRequired(false);
        element.setFormat(null);
        element.setOrder(0);
        element.setType(DEFAULT_TYPE);
        element.setValidators(Collections.emptyList());
        element.setFieldName(field);
        element.setFieldTranslation(context.getMessageTranslation(field));
        element.setShowLabel(true);
        element.setShowPlaceHolder(false);
        element.setDefaultValue(DEFAULT_EMPTY_VALUE);

        return element;
    }
    public static FormElement from(PojoFormElement formElement, String field, WebContext.LocalWebContext context) {
        FormElement element = new FormElement();
        element.setAutocomplete(formElement.autocomplete() ? "on" : "off");
        element.setPlaceHolder(formElement.placeHolder());
        element.setChecked(CheckableElement.from(formElement.checked()));
        element.setRequired(formElement.required());
        element.setFormat(formElement.format());
        element.setOrder(formElement.order());
        element.setType(formElement.type());
        element.setValidators(createValidators(formElement));
        element.setFieldName(field);
        element.setFieldTranslation(context.getMessageTranslation(field));
        element.setShowPlaceHolder(formElement.showPlaceHolder());
        element.setShowLabel(formElement.showLabel());
        element.setDefaultValue(formElement.defaultValue());
        return element;
    }

    @SneakyThrows
    private static List<FormValueValidator> createValidators(PojoFormElement element) {

        List<FormValueValidator> valueValidators = new ArrayList<>();
        if (element.required()){
            valueValidators.add( ValueNotEmptyValidator.DEFAULT);
        }

        for (Class <? extends FormValueValidator> cl : element.validators()){
            valueValidators.add(cl.getDeclaredConstructor().newInstance());
        }
        return valueValidators;
    }

    private Integer order;
    private String autocomplete;
    private FromElementValueConverter dataConverter;
    private String format;
    private CheckableElement checked;
    private String type;
    private String placeHolder;
    private boolean required;
    private List<FormValueValidator> validators;
    private String fieldName;
    private String fieldTranslation;
    private String defaultValue;
    private boolean showLabel;
    private boolean showPlaceHolder;


    @SneakyThrows
    public Object getValue(Object value) {

        Field field = value.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object result = field.get(value);
        field.setAccessible(false);
        return result;
    }
}
