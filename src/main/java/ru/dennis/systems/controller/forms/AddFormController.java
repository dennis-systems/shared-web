package ru.dennis.systems.controller.forms;

import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.entity.DefaultEntity;
import ru.dennis.systems.pojo_form.FormResolver;
import ru.dennis.systems.pojo_form.ValidationContext;
import ru.dennis.systems.service.AddFormService;
import ru.dennis.systems.config.WebConstants;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


public interface AddFormController< POJO_FORM extends Serializable, OBJECT extends DefaultEntity, SERVICE extends AddFormService<POJO_FORM, OBJECT>> extends  PageCheck, FormDependency<POJO_FORM> {


    @GetMapping(WebConstants.WEB_API_ADD)
    default String addJob(@RequestParam(required = false) String message, Model model) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        prePageCheck(null);
        model.addAttribute("isEdit", false);

       loadDefaultFormData(model);
        return WebConstants.asPage(getPath(), WebConstants.WEB_PAGE_ADD);
    }



    @PostMapping(WebConstants.WEB_API_ADD)
    default String addJob(@ModelAttribute POJO_FORM form) {
        prePageCheck(null);

        try {
            ValidationContext validationResults = new FormResolver().resolve(form, getContext());
            if (validationResults.isContainsErrors()){
                getContext().setAttribute(WebConstants.WEB_OBJECT_MODEL_ATTRIBUTE, form);
                return onSaveListener().validationErrors(false, null ,form, validationResults);
            }
            OBJECT data = getAddService().saveForm(form);
            return onSaveListener().onSave(true, data, form ,null);

        } catch (Exception e) {
            getLog().error("Could not add job", e);
            getContext().setAttribute(WebConstants.WEB_OBJECT_MODEL_ATTRIBUTE, form);
            return onSaveListener().onSave(false, null ,  form, e);
        }
    }

    default void preLoad(POJO_FORM form){};

    @SneakyThrows
    default void loadDefaultFormData(Model model){
        if (getContext().getAttribute(WebConstants.WEB_VALIDATION_CONTEXT_ATTRIBUTE) != null){
            model.addAttribute(WebConstants.WEB_VALIDATION_CONTEXT_ATTRIBUTE, getContext().getAttribute(WebConstants.WEB_VALIDATION_CONTEXT_ATTRIBUTE));
            getContext().removeAttribute(WebConstants.WEB_VALIDATION_CONTEXT_ATTRIBUTE);
        }
        POJO_FORM form = getContext().getAttribute(WebConstants.WEB_OBJECT_MODEL_ATTRIBUTE);
        if (form == null){
            form = (POJO_FORM) getFormClass().getConstructor().newInstance();
        }
        preLoad(form);
        model.addAttribute(WebConstants.WEB_FORM_FIELD_MODEL_ATTRIBUTE, FormResolver.from(form, getFormClass(), getContext()));
        model.addAttribute(WebConstants.WEB_OBJECT_MODEL_ATTRIBUTE, form);


        model.addAttribute(WebConstants.WEB_FORM_DESCRIPTOR_ATTRIBUTE, FormResolver.getFormDescription(form, WebConstants.asService(getPath() , WebConstants.WEB_API_ADD), getContext()));
        model.addAttribute(WebConstants.WEB_FORM_VALUE_ATTRIBUTE, FormResolver.getValues(form, getContext()));
        getContext().removeAttribute(WebConstants.WEB_OBJECT_MODEL_ATTRIBUTE);
    }

    WebContext.LocalWebContext getContext();

    Logger getLog();

    OnSaveListener<OBJECT, POJO_FORM> onSaveListener();

    SERVICE getAddService();

    String getPath();

}
