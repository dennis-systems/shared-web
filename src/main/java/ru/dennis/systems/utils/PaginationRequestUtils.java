package ru.dennis.systems.utils;


import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.pojo_view.PojoListView;
import ru.dennis.systems.pojo_view.PojoViewField;
import ru.dennis.systems.repository.DefaultSearchSpecification;
import ru.dennis.systems.repository.QueryCase;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

import static ru.dennis.systems.pojo_view.PojoViewField.SAME;


@Service
@Scope("singleton")
public class PaginationRequestUtils {

    public static final String NULL = "NONE";
    private static final String SEARCH_IDENTIFIER = "_search";
    private static final String SEARCH_IDENTIFIER_NOT_USED_OPERATOR = "_nu";
    private static final String SEARCH_OPERATOR_IDENTIFIER = "_so_";
    @Autowired
    Environment webConfig;

    @SneakyThrows
    public Sort getSorting(Optional<String> orderBy, Optional<Boolean> asc, Class<?> pojo) {
        PojoListView view = pojo.getAnnotation(PojoListView.class);
        String orderByParam = orderBy.orElse(null);
        if (orderByParam == null) {
            if (view != null) {
                return NULL.equals(view.defaultSortParam()) ? null :
                        Sort.by(view.defaultSortASC() ?
                                Sort.Order.asc(view.defaultSortParam()) :
                                Sort.Order.desc(view.defaultSortParam()));
            } else {
                String defaultField = webConfig.getProperty("shared.web.list.default_sorting", String.class, NULL);
                Boolean ascDefault = webConfig.getProperty("shared.web.list.default_sorting_asc", Boolean.class, Boolean.TRUE);

                if (NULL.equalsIgnoreCase(defaultField)) {
                    return null;
                }
                return Sort.by(
                        ascDefault ? Sort.Order.asc(
                                view.defaultSortParam()) : Sort.Order.desc(view.defaultSortParam()));

            }
        }
        boolean ascParam = asc.orElse(false);




        orderByParam = findOrderByParam(orderByParam, pojo);
        return Sort.by(ascParam ? Sort.Order.asc(orderByParam) : Sort.Order.desc(orderByParam));

    }

    @SneakyThrows
    private String findOrderByParam(String fieldName, Class<?> pojo) {
        try {
            Field field = pojo.getDeclaredField(fieldName);

            PojoViewField pojoViewField = field.getAnnotation(PojoViewField.class);

            if (pojoViewField != null && !SAME.equalsIgnoreCase(pojoViewField.dbField())) {
                return pojoViewField.dbField();
            }
        } catch ( Exception e){
            //TODO this is a problem of super class, we have to change it to go threw all super classes. Now just hot fix
            return fieldName;
        }
        return fieldName;

    }
    @SneakyThrows
    private void fillUpDefaults(QueryCase queryCase, String fieldName, Class<?> pojo) {

        try {
            Field field = pojo.getDeclaredField(fieldName);

            PojoViewField pojoViewField = field.getAnnotation(PojoViewField.class);

            if (pojoViewField != null && !PojoViewField.class.equals(pojoViewField.fieldClass())) {
                queryCase.setFieldClass(pojoViewField.fieldClass());
                queryCase.setComplex(pojoViewField.complex());
                queryCase.setJoinOn(pojoViewField.join());
            } else {
                queryCase.setFieldClass(field.getType());
                queryCase.setComplex(false);
                queryCase.setJoinOn(SAME);
            }
        }catch (Exception e){
            // ok don't do it
            return;
        }


    }

    public int getSize(Optional<Integer> size, Class<?> pojo) {

        PojoListView view = pojo.getAnnotation(PojoListView.class);
        if (view == null) {
            return size.orElse(webConfig.getProperty("shared.web.list.max_page_result", Integer.TYPE, 15));
        } else {
            return size.orElse(view.maxPageResults());
        }
    }

    public <T> Specification<T> getFilteringParams(WebContext.LocalWebContext context, Class<?> pojo) {
        return getFilteringParams(context, pojo, Collections.emptyList());
    }
    public <T> Specification<T> getFilteringParams(WebContext.LocalWebContext context, Class<?> pojo, List<QueryCase> additional) {
        return new DefaultSearchSpecification<>(getSearchParams(context, pojo, additional));
    }

    private List<QueryCase> getSearchParams(WebContext.LocalWebContext context, Class<?> pojo, List<QueryCase> additional) {
        if (context.httpParam("search") == null){
            return additional;
        }

        List<QueryCase> cases = new ArrayList<>();

        Map<String, String[]> params = context.getRequest().getParameterMap();
        params.keySet().stream().filter(key -> key.endsWith(SEARCH_IDENTIFIER)).forEach(key -> {
            String field = key.replace(SEARCH_IDENTIFIER, "");
            String value = context.httpParam(key);
            String type = context.httpParam(field + SEARCH_OPERATOR_IDENTIFIER);
            if (!SEARCH_IDENTIFIER_NOT_USED_OPERATOR.equalsIgnoreCase(type)){
                QueryCase queryCase = new QueryCase();
                queryCase.setField(findOrderByParam(field, pojo));
                queryCase.setOperator(type);
                fillUpDefaults(queryCase, field, pojo);
                queryCase.setValue(value);
                cases.add(queryCase);
            }

        });
        cases.addAll(additional);

        return cases;
    }
}
