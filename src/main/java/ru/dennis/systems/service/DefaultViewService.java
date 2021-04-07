package ru.dennis.systems.service;

import de.allianz.bpe.shared.exceptions.*;
import ru.dennis.systems.exceptions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class DefaultViewService<T> implements AbstractService<T> {

    @Override
    public List<T> find() {
        return StreamSupport.stream(getRepository().findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public T edit(T object) throws ItemNotUserException, ItemNotFoundException, UnmodifiedItemSaveAttempt, ItemDoesNotContainsIdValue {
         throw new UnsupportedOperationException("Update operations are blocked by views");
    }

    @Override
    public void delete(Long id) throws ItemNotUserException, ItemNotFoundException {
        throw new UnsupportedOperationException("delete operations are blocked by views");
    }

    @Override
    public void deleteItems(List<Long> ids) throws ItemNotUserException, ItemNotFoundException {
        throw new UnsupportedOperationException("delete operations are blocked by views");
    }

    @Override
    public boolean exists(T object) {
        throw new UnsupportedOperationException("exists operations are blocked by views");
    }

    @Override
    public T save(T form) throws ItemForAddContainsIdException {
        throw new UnsupportedOperationException("Update operations are blocked by views");
    }

    @Override
    public Optional<T> findById(Long id) {
        throw new UnsupportedOperationException("find by Id operations are blocked by views");
    }
}
