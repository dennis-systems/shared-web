package ru.dennis.systems.controller.forms;

import ru.dennis.systems.service.DeleteService;
import ru.dennis.systems.service.OnDeleteFormElement;
import ru.dennis.systems.config.WebConstants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface DeleteFormController {
    @PostMapping(WebConstants.WEB_API_DELETE)
    default String deleteJob(@PathVariable("id") String id, Model model) {


        try {
            deleteService().deleteById(id);
            return onDeleteFormElement().onDelete(true, id, null);

        } catch (Exception e) {
            return onDeleteFormElement().onDelete(false, id, e);
        }

    }

    OnDeleteFormElement onDeleteFormElement();

    DeleteService deleteService();
}
