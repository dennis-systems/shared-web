package ru.dennis.systems.service;

import ru.dennis.systems.controller.forms.PageCheck;
import ru.dennis.systems.exceptions.ItemForAddContainsIdException;
import ru.dennis.systems.repository.PaginationRepository;

import java.util.Optional;

public interface AddFormService<FORM_POJO, T>  extends PageCheck {

    default T saveForm(FORM_POJO form) throws ItemForAddContainsIdException {
        return save(formToPojo(form), false);
    }

    default T editForm(FORM_POJO form) throws ItemForAddContainsIdException {
        return save(formToPojo(form), true);
    }

    default T save(T object, boolean edit) {
        preAdd(object, edit);
        return getRepository().save(object);
    }

    default T preAdd(T object, boolean edit){
        return object;
    }

    PaginationRepository<T> getRepository();

    default Optional<T> findById(Long id) {
        return getRepository().findById(id);
    }

    default T formToPojo(FORM_POJO form) {
        return (T) form;
    }

}
