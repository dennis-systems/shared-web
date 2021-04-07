package ru.dennis.systems.service;

import ru.dennis.systems.entity.DefaultEntity;
import ru.dennis.systems.exceptions.ItemForAddContainsIdException;
import ru.dennis.systems.exceptions.ItemNotFoundException;
import ru.dennis.systems.exceptions.ItemNotUserException;
import ru.dennis.systems.exceptions.UnmodifiedItemSaveAttempt;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation for the AbstractService
 *
 * @param <T>
 */
@Slf4j
public abstract class DefaultRepoServiceImpl<T extends DefaultEntity> implements AbstractService<T> {


    @Override
    @Deprecated
    /**
     *
     * Returns all records from db
     * @deprecated since version 1.0.1 as need to be used with the limit
     * Default method to get all values should be used with care or better not to be used!
     *
     */
    public List<T> find() {
        return find(null ,null, 0);
    }



    @Override
    public T save(T object) throws ItemForAddContainsIdException {
        if (object == null) {
            throw new IllegalArgumentException("Can not save null Object");
        }
        object = preAdd(object);
        return afterAdd(getRepository().save(object));
    }


    @Override
    public T edit(T object) throws ItemNotFoundException, UnmodifiedItemSaveAttempt {
        if (object == null) {
            throw new IllegalArgumentException("Can not save null Object");
        }
        T original = getRepository().findById(object.getId()).orElseThrow(() -> new ItemNotFoundException(object.getId()));
        return getRepository().save(preEdit(object, original));
    }

    @Override
    public void delete(Long id) {
        getRepository().deleteById(id);
    }

    @Override
    public void deleteItems(List<Long> ids) throws ItemNotUserException, ItemNotFoundException {
        for (Long id : ids) {
            getRepository().deleteById(id);
        }
    }

    @Override
    public boolean exists(T object) {

        if (object != null) {
            return getRepository().existsById(object.getId());
        }
        throw new IllegalArgumentException("Entity should be be not null instance");
    }

    @Override
    public Optional<T> findById(Long id) {
        return getRepository().findById(id);
    }
}
