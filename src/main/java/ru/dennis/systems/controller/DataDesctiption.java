package ru.dennis.systems.controller;

import ru.dennis.systems.pojo_view.PojoListView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class DataDesctiption {
    private String title;
    private String component_id;
    private Boolean isSearchable;

    public static DataDesctiption of(String titleKey, String id, Class<?> pojo) {
        PojoListView view = pojo.getAnnotation(PojoListView.class);
        boolean isSearchable = view == null ?  true : view.enableSearching();

        return new DataDesctiption(titleKey, id, isSearchable );
    }
}
