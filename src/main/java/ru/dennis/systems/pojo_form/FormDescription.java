package ru.dennis.systems.pojo_form;

import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.pojo_view.UIAction;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class FormDescription {

    public static FormDescription createDefault(Class<?> c, String path, WebContext.LocalWebContext context) {
        FormDescription description = new FormDescription();
        description.showTitle = true;
        description.setTitle(context.getMessageTranslation("froms." + c.getSimpleName() + ".title"));
        description.setEditTitle(context.getMessageTranslation("froms." + c.getSimpleName() + "edit.title"));
        description.setActions(Collections.emptyList());
        description.setTitleClassName("form_title");
        description.setMethod("POST");
        description.setId(c.getSimpleName());
        description.setCommitButtonText(context.getMessageTranslation("submit"));
        description.setPath(path);
        return description;
    }

    private String method;
    private String title;
    private List<UIAction> actions;
    private boolean showTitle;
    private String titleClassName;
    private String commitButtonText;
    private String id;
    private String action;
    private String path;
    private String editTitle;

    public static FormDescription from(PojoForm form, Class<?> c, String path, WebContext.LocalWebContext context) {
        FormDescription description = new FormDescription();
        description.setShowTitle(true);
        description.setTitle(context.getMessageTranslation(form.title().title()));
        description.setEditTitle(context.getMessageTranslation(form.title().editTitle()));
        description.setActions(Arrays.asList(form.formActions()));
        description.setId(form.id());
        description.setMethod(form.method());
        description.setShowTitle(form.title().show());
        description.setTitleClassName(form.title().className());
        description.setCommitButtonText(context.getMessageTranslation(form.commitButtonText()));
        description.setPath(path);
        description.setEditTitle(context.getMessageTranslation(form.title().editTitle()));

        return description;
    }
}
