package ru.dennis.systems.pojo_view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface PojoListView {
    boolean enableSearching() default false;
    String defaultSortParam() default "id";
    boolean defaultSortASC() default true;

    int maxPageResults() default 15;
}
