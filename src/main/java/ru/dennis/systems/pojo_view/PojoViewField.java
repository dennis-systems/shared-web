package ru.dennis.systems.pojo_view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface PojoViewField {

     String SAME = "SAME_OBJECT_AS_FIELD_NAME";
     boolean visible() default true;
     boolean searchable() default true;
     boolean sortable() default true;
     boolean visibleInObjectView() default true;
     int order() default 0;
     UIAction[] actions() default {};
     Class<? extends DefaultDataConverter> dataConverter() default DefaultDataConverter.class;
     boolean showContent() default true;
     String format() default "";
     String dbField() default SAME;
     String join() default SAME;
     boolean complex() default false;
     String searchType() default DEFAULT_TYPES.TEXT;
     Class<?> fieldClass() default PojoViewField.class;
}
