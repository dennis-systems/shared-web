package ru.dennis.systems.controller.items;

import ru.dennis.systems.entity.DefaultEntity;
import ru.dennis.systems.exceptions.ItemDoesNotContainsIdValue;
import ru.dennis.systems.exceptions.ItemNotFoundException;
import ru.dennis.systems.exceptions.ItemNotUserException;
import ru.dennis.systems.exceptions.UnmodifiedItemSaveAttempt;
import ru.dennis.systems.service.AbstractService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Update object interface. Implementing this interface, will automatically create ../edit/
 * @param <DB_TYPE> Entity which is managed by this interface and Service
 * @param <SERVICE> a service, wich extends AbstractService

 */
@RestController
public interface EditItemController<DB_TYPE, FORM_TYPE extends DefaultEntity, SERVICE extends AbstractService<DB_TYPE>> extends Transformable<FORM_TYPE, DB_TYPE> {
    @PutMapping(value = "/edit")
    default DB_TYPE edit(@RequestBody FORM_TYPE object) throws ItemNotUserException, ItemDoesNotContainsIdValue, ItemNotFoundException, UnmodifiedItemSaveAttempt {
             return getService().edit(fromForm(object));
    }

    SERVICE getService();
}
