package ru.dennis.systems.controller.items;


import ru.dennis.systems.service.AbstractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * List  object interface. Implementing this interface, will automatically create ../list/
 *
 * @param <DB_TYPE>       Entity which is managed by this interface and Service
 * @param <SERVICE> a service, which extends AbstractService
 */
@RestController
public interface ListItemController<DB_TYPE, FORM_TYPE, SERVICE extends AbstractService<DB_TYPE>> extends Transformable<FORM_TYPE, DB_TYPE> {

    @GetMapping("/list")
    @ResponseBody

    default ResponseEntity<List<FORM_TYPE>> get(@RequestParam(value = "from", required = false) Long from,
                                                @RequestParam (value = "limit", required = false) Integer limit,
                                                @RequestParam(value = "page", required = false) Integer page) {

        List<DB_TYPE> data = getService().find(from, limit, page);
        List<FORM_TYPE> result= new ArrayList<>();
        for (DB_TYPE item : data){
            result.add(toForm(item));
        }

        return ResponseEntity.ok(result);

    }

    SERVICE getService();
}
