package ru.dennis.systems.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryCase {
    public static final String EQUALS_OPERATOR = "equals";
    public static final String STARTS_WITH_OPERATOR = "startsWith";
    public static final String ENDS_WITH_OPERATOR = "endsWith";
    public static final String MORE_THEN = "gt";
    public static final String LESS_THEN = "lt";
    public static final String LESS_EQUALS = "le";
    public static final String MORE_EQUALS = "ge";
    public static final String CONTAINS_OPERATOR = "contains";
    public static final String IN = "in";
    public static final String NOT_CONTAINS = "_nc_";
    public static final String NOT_EMPTY = "_ne_";
    public static final String EMPTY = "_em_";
    public static final String NOT_EQUALS_OPERATOR = "notEquals";
    public static final String NULL_OPERATOR = "null";
    public static final String NOT_NULL_OPERATOR = "notNull";
    private Class<?> fieldClass;
    private Object value;
    private String field;
    private String operator = EQUALS_OPERATOR;
    private boolean isComplex = false;
    private String joinOn;
    private  Object parameter;


    public QueryCase(String value, String field, Class<?> fieldClass) {
        this.value = value;
        this.field = field;
        this.fieldClass = fieldClass;
    }

    public static QueryCase equalsOf(String field, Object value) {
        QueryCase queryCase = new QueryCase();

        queryCase.setOperator(EQUALS_OPERATOR);
        queryCase.setField(field);
        queryCase.setValue(value);
        queryCase.setFieldByValue(value);

        return queryCase;
    }

    public static QueryCase notEqualsOf(String field, Object value) {
        QueryCase queryCase = new QueryCase();

        queryCase.setOperator(NOT_EQUALS_OPERATOR);
        queryCase.setField(field);
        queryCase.setValue(value);
        queryCase.setFieldByValue(value);
        
        return queryCase;
    }

    public static QueryCase equalsOfInSensitive(String field, Object value) {
        QueryCase queryCase = new QueryCase();
        if (value != null && ! (value instanceof String)){
            throw new IllegalArgumentException("Only strings are accepted!");
        }
        queryCase.setOperator(EQUALS_OPERATOR);
        queryCase.setField(field);
        queryCase.setValue(value);
        queryCase.setFieldByValue(value);
        queryCase.setParameter(true);
        queryCase.string();

        return queryCase;
    }

    public static QueryCase containsOfInSensitive(String field, Object value) {
        QueryCase queryCase = new QueryCase();

        if (value != null && ! (value instanceof String)){
            throw new IllegalArgumentException("Only strings are accepted!");
        }

        queryCase.setOperator(CONTAINS_OPERATOR);
        queryCase.setField(field);
        queryCase.setValue(value);
        queryCase.setFieldByValue(value);
        queryCase.setParameter(true);
        queryCase.string();

        return queryCase;
    }

    private void setFieldByValue(Object value) {
        if (value != null){
            setFieldClass(value.getClass());
        }
    }

    public static QueryCase lessOrEquals(String field, Object value) {
        QueryCase queryCase = new QueryCase();

        queryCase.setOperator(LESS_EQUALS);
        queryCase.setField(field);
        queryCase.setValue(value);
        queryCase.setFieldByValue(value);
        return queryCase;
    }

    public static QueryCase moreOrEquals(String field, Object value) {
        QueryCase queryCase = new QueryCase();

        queryCase.setOperator(MORE_EQUALS);
        queryCase.setField(field);
        queryCase.setValue(value);
        queryCase.setFieldByValue(value);
        return queryCase;
    }

    public static QueryCase ofNull(String field) {
        QueryCase queryCase = new QueryCase();

        queryCase.setOperator(NULL_OPERATOR);
        queryCase.setField(field);
        return queryCase;
    }

    public static QueryCase ofNotNull(String field) {
        QueryCase queryCase = new QueryCase();
        queryCase.setOperator(NOT_NULL_OPERATOR);
        queryCase.setField(field);
        return queryCase;
    }

    public static QueryCase in(String field, List<?> value) {
        QueryCase queryCase = new QueryCase();

        queryCase.setOperator(IN);
        queryCase.setField(field);
        queryCase.setFieldByValue(value);
        return queryCase;
    }

    public QueryCase complex(String join) {
        setComplex(true);
        setJoinOn(join);
        return this;
    }

    public QueryCase string() {
        setFieldClass(String.class);
        return this;
    }

    public QueryCase date() {
        setFieldClass(Date.class);
        return this;
    }

    public QueryCase integer() {
        setFieldClass(Integer.class);
        return this;
    }

    @SuppressWarnings("deprecation")
    public <T>DefaultSearchSpecification<T> specification(){
        return new DefaultSearchSpecification<T>(this);
    }
}

