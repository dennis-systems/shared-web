package ru.dennis.systems.pojo_form;

import ru.dennis.systems.config.WebContext;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class FormResolver {
    public static FormDescription getFormDescription(Serializable form, String path, WebContext.LocalWebContext context) {
        PojoForm annotation = form.getClass().getAnnotation(PojoForm.class);

        if (annotation == null) {
            return FormDescription.createDefault(form.getClass(), path, context);
        }
        return FormDescription.from(annotation, form.getClass(), path, context);
    }

    public ValidationContext resolve(Serializable form, WebContext.LocalWebContext webContext) {
        ValidationContext context = new ValidationContext();
        Map<String, List<ValidationResult>> data = new HashMap<>();
        List<FormElement> elements = from(form, form.getClass(), webContext);
        for (FormElement element : elements) {
            if (element.getValidators().size() > 0) {
                List<ValidationResult> results = new ArrayList<>();
                for (FormValueValidator validator : element.getValidators()) {
                    ValidationResult result = validator.validate(form, element.getFieldName(), element.getValue(form), webContext);
                    results.add(result);
                    if (!result.getResult()) {
                        context.setContainsErrors(true);
                    }
                }
                data.put(element.getFieldName(), results);
            } else {
                data.put(element.getFieldName(), ValidationResult.OK);
            }
        }

        context.setData(data);

        return context;
    }

    @SneakyThrows
    public static Map<String, Object> getValues(Serializable form, WebContext.LocalWebContext webContext) {

        Map<String, Object> data = new HashMap<>();
        List<FormElement> elements = from(form, form.getClass(), webContext);
        for (FormElement element : elements) {

            Field field = form.getClass().getDeclaredField(element.getFieldName());

            field.setAccessible(true);
            data.put(element.getFieldName(), field.get(form));
            field.setAccessible(false);

        }


        return data;
    }

    @SneakyThrows
    public static List<FormElement> from(Serializable form, Class formClass, WebContext.LocalWebContext context) {
        if (form == null) {
            form = (Serializable) formClass.getDeclaredConstructor().newInstance();
        }
        Field[] declaredFields = form.getClass().getDeclaredFields();
        List<FormElement> formElements = new ArrayList<>();
        Arrays.stream(declaredFields).forEach(field -> {
            //ignore constants
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {

            } else {
                PojoFormElement element = field.getAnnotation(PojoFormElement.class);
                if (element == null) {
                    formElements.add(FormElement.createDefault(field.getName(), context));
                    return;
                }
                if (!element.visible()) {
                    return;
                }
                formElements.add(FormElement.from(element, field.getName(), context));
            }
        });
        formElements.sort(Comparator.comparing(FormElement::getOrder));
        return formElements;
    }
}
