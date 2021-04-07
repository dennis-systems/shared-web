package ru.dennis.systems.controller.items;


import ru.dennis.systems.exceptions.ItemForAddContainsIdException;
import ru.dennis.systems.service.AbstractService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * An interface which adds possibility to add data
 *
 * @Requires a service, to be be proceed, which is accessed threw the getService method.
 */
@RestController
public interface AddItemController<DB_TYPE, FORM_TYPE, SERVICE extends AbstractService<DB_TYPE>> extends Transformable<FORM_TYPE, DB_TYPE> {

    /**
     * Performs add procedure
     * @param model
     * @return an object which had been saved in DB with it's ID
     * @throws ItemForAddContainsIdException - this exception, means that object is tried to be updated instead of added. To devide add and edit is necessery as two different opertations with different privileges
     */
    @PostMapping(value = "/add")
    @ResponseBody
    default ResponseEntity<DB_TYPE> add(@RequestBody FORM_TYPE model) throws ItemForAddContainsIdException {
        return ResponseEntity.ok( getService().save(fromForm(model)));
    }


    @PostMapping(value = "/addAll")
    @ResponseBody
    default ResponseEntity<Iterable<DB_TYPE>> setAll(@RequestBody List<FORM_TYPE> data) throws ItemForAddContainsIdException {

        List<DB_TYPE> result = new ArrayList<>();
        for (FORM_TYPE element : data){
            DB_TYPE item = fromForm(element);
            try {
                DB_TYPE savedItem = getService().save(item);
                result.add(savedItem);
            } catch (Exception e){
                getLogger().error("Error on adding element: ", e);
            }
        }

        return ResponseEntity.ok( result );
    }


    Logger getLogger();

    SERVICE getService();

}
