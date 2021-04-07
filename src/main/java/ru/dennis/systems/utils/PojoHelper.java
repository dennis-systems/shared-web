package ru.dennis.systems.utils;

import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.pojo_view.DefaultDataConverter;
import ru.dennis.systems.pojo_view.PojoViewField;
import ru.dennis.systems.pojo_view.UIAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.data.domain.Page;

import java.lang.reflect.Field;
import java.util.*;

import static ru.dennis.systems.pojo_view.DEFAULT_TYPES.TEXT;

@Slf4j
public class PojoHelper {
    public static List<PojoHeader> getHeaders(final Class<?> jobReportClass) {

        final List<PojoHeader> headers = new ArrayList<>();
        Field[] declaredFields = jobReportClass.getDeclaredFields();

        for (Field field : declaredFields) {
            PojoViewField pojoViewField = field.getAnnotation(PojoViewField.class);
            if (pojoViewField != null) {
                headers.add(new PojoHeader(field.getName(),
                        null, pojoViewField.order(),
                        field.getName(),
                        pojoViewField.format() ,
                        pojoViewField.dataConverter(),
                        getActions(pojoViewField),
                        pojoViewField.sortable()  ,
                        pojoViewField.searchable(),
                        pojoViewField.visible(),
                        pojoViewField.searchType(),
                        pojoViewField.showContent()));
            } else {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())){

                    headers.add(new PojoHeader(field.getName(), null , 0, field.getName(), "" , DefaultDataConverter.class, null , true, true, true , TEXT ,  true));
                }
            }


        }

        return headers;
    }

    private static UIAction[] getActions(PojoViewField pojoViewField) {
        return pojoViewField.actions();
    }

    public static Object addData(PojoHeader header, Object dataObject) {
        String field = header.getField();
        Class<?> cz = dataObject.getClass();
        try {
            Field f = cz.getDeclaredField(field);

            f.setAccessible(true);
            Object value = f.get(dataObject);

            if (header.getDataConverter() != DefaultDataConverter.class) {
                DefaultDataConverter converter = (DefaultDataConverter) header.getDataConverter().getDeclaredConstructor().newInstance();
                value = converter.convert(value, dataObject);
            }

            f.setAccessible(false);

            return value;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<Map<String, Object>> toMap(List<PojoHeader> headers, Page<?> values, WebContext.LocalWebContext context){
        var listResult = new ArrayList<Map<String, Object>>();
        sortData(headers);
        for (Object object : values) {
            var result = new LinkedMap<String, Object>();
            for (PojoHeader header : headers) {
                result.put(PojoHeader.get(header, context, false), addData(header, object));
            }
            listResult.add(result);
        }

        return listResult;

    }

    public static void sortData(List<PojoHeader> headers) {
        headers.sort(Comparator.comparing(PojoHeader::getOrder));
    }
}
