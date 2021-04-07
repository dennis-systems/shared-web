package ru.dennis.systems.pojo_form;

import ru.dennis.systems.pojo_view.UIAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface PojoForm {
    UIAction [] formActions() default {};
    FormTitle title();
    String commitButtonText() default "submit";
    String id() default "PojoForm";
    String method() default "Post";
}
