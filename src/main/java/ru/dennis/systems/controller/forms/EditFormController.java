package ru.dennis.systems.controller.forms;

import ru.dennis.systems.entity.DefaultEntity;
import ru.dennis.systems.exceptions.ItemNotFoundException;
import ru.dennis.systems.service.AddFormService;
import ru.dennis.systems.config.WebConstants;
import lombok.SneakyThrows;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;

public interface EditFormController<POJO_FORM extends Serializable, T extends DefaultEntity, ADD_SERVICE extends AddFormService<POJO_FORM, T>>
        extends AddFormController<POJO_FORM, T, ADD_SERVICE > {
    @SneakyThrows
    @GetMapping(WebConstants.WEB_API_EDIT)
    default String addEdit(@PathVariable(name = "id") Long id, Model model) {
        prePageCheck(id);
        T object = getAddService().findById(id).orElseThrow(()-> new ItemNotFoundException(id));
        if (object == null){
            return WebConstants.withMessage("xray.cannot.find.object", WebConstants.asPage(getPath(), WebConstants.WEB_API_LIST));
        }


        getContext().setAttribute(WebConstants.WEB_OBJECT_MODEL_ATTRIBUTE, toForm(object));
        loadDefaultFormData(model);

        model.addAttribute("isEdit", true);

        return WebConstants.asPage(getPath(), WebConstants.WEB_PAGE_ADD);
    }



    String getPath();

    POJO_FORM toForm(T object);
}
