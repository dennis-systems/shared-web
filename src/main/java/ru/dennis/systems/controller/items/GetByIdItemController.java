package ru.dennis.systems.controller.items;

import ru.dennis.systems.exceptions.ItemNotFoundException;
import ru.dennis.systems.service.AbstractService;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface GetByIdItemController <DB_TYPE, FORM_TYPE, SERVICE extends AbstractService<DB_TYPE>>  {
    @SneakyThrows
    @GetMapping("/id/{id}")
    @ResponseBody
    default ResponseEntity<FORM_TYPE> get(@PathVariable ("id") Long id) {
        DB_TYPE type = getService().findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        if (type == null){
            throw new ItemNotFoundException(id);
        }
        return ResponseEntity.ok(toForm(type));
    }

    FORM_TYPE toForm(DB_TYPE item);

    SERVICE getService();
}
