package ru.dennis.systems.utils;

import ru.dennis.systems.pojo_view.UIAction;
import ru.dennis.systems.config.WebContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PojoHeader implements Comparable<PojoHeader> {
    private String header;
    private String translation;
    private Integer order;
    private String field;
    private String format;
    private Class<?> dataConverter;
    private UIAction[] actions;
    private Boolean sortable;
    private Boolean searchable;
    private boolean visible;
    private String searchType;
    boolean showContent;

    public static List<String> get(
            List<PojoHeader> headers,
            WebContext.LocalWebContext context, boolean toTranslate) {

        if( headers == null){
            return Collections.emptyList();
        }

        PojoHelper.sortData(headers);
        List<String> list = new ArrayList<>();
        for (PojoHeader pojoHeader : headers) {

             list.add(get(pojoHeader,context, toTranslate));
        }
        return list;
    }

    public static String get(PojoHeader header,
                             WebContext.LocalWebContext context, boolean toTranslate){
        if( header == null){
            return Strings.EMPTY;
        }
        if (toTranslate) {
            return context.getMessageTranslation( header.header);
        } else {
            header.setTranslation(context.getMessageTranslation(header.header));
            return header.header;
        }
    }

    @Override
    public int compareTo(PojoHeader o) {
        if (o == null) return 0;
        return order == null ? 0 : order.compareTo(o.order);
    }
}
