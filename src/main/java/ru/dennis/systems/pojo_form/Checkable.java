package ru.dennis.systems.pojo_form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Checkable {
    boolean isCheckElement ();
    String value() default "";
    boolean checked() default false;

}
