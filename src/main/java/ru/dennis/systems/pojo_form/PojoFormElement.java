package ru.dennis.systems.pojo_form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.dennis.systems.pojo_view.DEFAULT_TYPES.DEFAULT_EMPTY_VALUE;
import static ru.dennis.systems.pojo_view.DEFAULT_TYPES.DEFAULT_TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface PojoFormElement {



     boolean visible() default true;
     int order() default 0;
     boolean autocomplete() default true;
     Class<? extends FromElementValueConverter> dataConverter() default FromElementValueConverter.class;
     String format() default "";
     Checkable checked() default @Checkable(isCheckElement = false);
     String type() default DEFAULT_TYPE;
     String placeHolder() default DEFAULT_EMPTY_VALUE;
     boolean required() default false;
     Class<? extends FormValueValidator>[] validators() default {};

      String defaultValue() default "";
      boolean showLabel() default true;
      boolean showPlaceHolder() default false;


}
