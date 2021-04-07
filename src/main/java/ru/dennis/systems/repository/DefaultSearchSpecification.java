package ru.dennis.systems.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("ALL")
@Slf4j
public class DefaultSearchSpecification<T> implements Specification<T> {

    private List<QueryCase> params;
    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    private boolean distinct = false;

    public DefaultSearchSpecification(List<QueryCase> params) {
        this.params = params;
    }

    public DefaultSearchSpecification(List<QueryCase> params, boolean distinct) {
        this.params = params;
        this.distinct = distinct;
    }

    public DefaultSearchSpecification(){
        this.params = new ArrayList<>();

    }

    public DefaultSearchSpecification<T> addCase(QueryCase queryCase){
        this.params.add(queryCase);
        return this;
    }

    @Deprecated
    /**
     * Use {@link QueryCase#specification()} medthod instead
     */
    public DefaultSearchSpecification(QueryCase queryCase) {
        this.params = new ArrayList<>();
        addCase(queryCase);
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        query.distinct(distinct);
        for (QueryCase queryCase : params) {
            ParameterExpression parameter = criteriaBuilder.parameter(queryCase.getFieldClass());

            Object value = queryCase.getValue();

            if (Date.class.equals(queryCase.getFieldClass()) && !Date.class.isAssignableFrom(value.getClass())
                    && isNotNullCase(queryCase)) {
                try {
                    value = formatter.parse(String.valueOf(queryCase.getValue()));
                } catch (Exception e) {
                    log.warn("Search is not able to parse date: " + value, e);
                    continue;
                }
            }

            try {
                if (isNotNullCase(queryCase) && (Number.class.isAssignableFrom(queryCase.getFieldClass()))) {
                    try {
                        value = getNumberFromString(String.valueOf( queryCase.getValue()), queryCase.getFieldClass());
                    } catch (Exception e) {
                        log.warn("Search is not able to parse long: " + value, e);
                        continue;
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }

            if (String.class.equals(parameter.getParameterType())) {

                Boolean selectInsensitive = true;

                try {
                    selectInsensitive = (boolean)queryCase.getParameter();
                } catch (Exception ignored){

                }

                if (QueryCase.CONTAINS_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {

                    if (selectInsensitive) {
                        predicates.add(criteriaBuilder.like(getPath(root, queryCase), "%" + queryCase.getValue() + "%"));
                    } else {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getPath(root, queryCase)), "%" + queryCase.getValue().toString().toLowerCase() + "%"));
                    }
                }

                if (QueryCase.EQUALS_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {

                    if (selectInsensitive) {
                        predicates.add(criteriaBuilder.equal(getPath(root, queryCase), value));
                    } else {
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(getPath(root, queryCase)), value.toString().toLowerCase()));
                    }
                }
                if (QueryCase.STARTS_WITH_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.like(getPath(root, queryCase), queryCase.getValue() + "%"));
                }
                if (QueryCase.ENDS_WITH_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.like(getPath(root, queryCase), "%" + queryCase.getValue()));
                }
            }

            if (Date.class.equals(queryCase.getFieldClass())) {
                if (QueryCase.LESS_THEN.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.lessThan(getPath(root, queryCase), (Date) value));
                }
                if (QueryCase.MORE_THEN.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.greaterThan(getPath(root, queryCase), (Date) value));
                }
                if (QueryCase.LESS_EQUALS.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(getPath(root, queryCase), (Date) value));
                }
                if (QueryCase.MORE_EQUALS.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(getPath(root, queryCase), (Date) value));
                }
            }

            if (Collection.class.isAssignableFrom(queryCase.getFieldClass())) {
                Expression<Collection<String>> collectionExpression = getPath(root, queryCase);
                if (QueryCase.CONTAINS_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.isMember(String.valueOf(queryCase.getValue()), collectionExpression));
                }
                if (QueryCase.NOT_CONTAINS.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.isNotMember(String.valueOf(queryCase.getValue()), collectionExpression));
                }
                if (QueryCase.NOT_EMPTY.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.isNotEmpty(collectionExpression));
                }
                if (QueryCase.EMPTY.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.isEmpty(collectionExpression));
                }
            }

            if (QueryCase.IN.equalsIgnoreCase(queryCase.getOperator())) {
                CriteriaBuilder.In inClause = criteriaBuilder.in(getPath(root, queryCase));

                Collection cValue = (Collection) value;
                cValue.forEach((x)-> inClause.value(x) );
                predicates.add(inClause);
            }

            if (Number.class.isAssignableFrom(queryCase.getFieldClass()) || isPrimitiveNumber(queryCase.getFieldClass())) {
                if (QueryCase.LESS_THEN.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.lt(getPath(root, queryCase), (Number) value));
                }
                if (QueryCase.MORE_THEN.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.gt(getPath(root, queryCase), (Number) value));
                }

                if (QueryCase.LESS_EQUALS.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.le(getPath(root, queryCase), (Number) value));
                }
                if (QueryCase.MORE_EQUALS.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.ge(getPath(root, queryCase), (Number) value));
                }
                if (QueryCase.NOT_EQUALS_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.notEqual(getPath(root, queryCase), (Number) value));
                }

                if (QueryCase.EQUALS_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                    predicates.add(criteriaBuilder.equal(getPath(root, queryCase), value));
                }

            }

            if (QueryCase.NOT_NULL_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                predicates.add(criteriaBuilder.isNotNull(getPath(root, queryCase)));
            }
            if (QueryCase.NULL_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                predicates.add(criteriaBuilder.isNull(getPath(root, queryCase)));
            }
            if (QueryCase.EQUALS_OPERATOR.equalsIgnoreCase(queryCase.getOperator()) && !String.class.isAssignableFrom(queryCase.getValue().getClass())) {
                predicates.add(criteriaBuilder.equal(getPath(root, queryCase), value));
            }

            if (QueryCase.NOT_EQUALS_OPERATOR.equalsIgnoreCase(queryCase.getOperator())) {
                predicates.add(criteriaBuilder.notEqual(getPath(root, queryCase), value));
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private boolean isPrimitiveNumber(Class c) {
        return int.class == c || long.class == c || short.class == c || double.class == c || float.class == c ;
    }

    private <T> Expression<T> getPath(Root root, QueryCase queryCase) {
        if (!queryCase.isComplex()) {
            return root.get(queryCase.getField());
        }

        String[] paths = queryCase.getJoinOn().split("\\.");
        Join join = null;
        for (String j : paths) {
            if (join == null) {
                join = root.join(j);
            } else {
                join = join.join(j);
            }
        }

        return join.get(queryCase.getField());
    }

    private Number getNumberFromString(String value, Class<?> fieldClass) {

        if (Integer.class.equals(fieldClass)) {
            return Integer.valueOf(value);
        }
        if (Double.class.equals(fieldClass)) {
            return Double.valueOf(value);
        }
        if (Long.class.equals(fieldClass)) {
            return Long.valueOf(value);
        }
        if (Short.class.equals(fieldClass)) {
            return Short.valueOf(value);
        }

        if (BigInteger.class.equals(fieldClass)) {
            return BigInteger.valueOf(Long.valueOf(value));
        }

        throw new IllegalArgumentException(" cannot transform String to number! ");
    }

    private boolean isNotNullCase(QueryCase queryCase) {
        return !QueryCase.NOT_NULL_OPERATOR.equalsIgnoreCase(queryCase.getOperator())
                && !QueryCase.NULL_OPERATOR.equalsIgnoreCase(queryCase.getOperator());
    }
}