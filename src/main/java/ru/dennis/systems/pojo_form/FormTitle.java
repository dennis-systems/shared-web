package ru.dennis.systems.pojo_form;

import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface FormTitle {
    boolean show() default true;
    String title() default Strings.EMPTY;
    String className() default Strings.EMPTY;
    String editTitle() default Strings.EMPTY;
}
