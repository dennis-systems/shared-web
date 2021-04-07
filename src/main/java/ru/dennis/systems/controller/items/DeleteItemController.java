package ru.dennis.systems.controller.items;

import ru.dennis.systems.exceptions.ItemNotFoundException;
import ru.dennis.systems.exceptions.ItemNotUserException;
import ru.dennis.systems.service.AbstractService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Delete object interface. Implementing this interface, will automatically create ../delete/{id}
 * @param <T> Entity which is managed by this interface and Service
 * @param <SERVICE> a service, wich extends AbstractService
 */
@RestController
public interface DeleteItemController<T, SERVICE extends AbstractService<T>> {

    @DeleteMapping(path = "/delete/{id}")
    default void delete(@PathVariable Long id) throws ItemNotUserException, ItemNotFoundException {
        getService().delete(id);
    }

    @DeleteMapping(value = "/deleteItems")
    default void deleteItems(@RequestParam List<Long> ids) throws ItemNotUserException, ItemNotFoundException {
        getService().deleteItems(ids);
    }

    SERVICE getService();
}
