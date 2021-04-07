package ru.dennis.systems.service;

import de.allianz.bpe.shared.exceptions.*;
import ru.dennis.systems.entity.DefaultEntity;
import ru.dennis.systems.repository.DefaultSearchSpecification;
import ru.dennis.systems.repository.PaginationRepository;
import ru.dennis.systems.repository.QueryCase;
import org.springframework.data.domain.PageRequest;
import ru.dennis.systems.exceptions.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation should implement methods which are necessary for web service to work properly
 * In this implementation all methods, like add, edit, delete or list are in the same interface. Form more complex functionality, it can make sence to move all this logical functions to the separate service
 * @param <T> A class of the entity to be saved. Should be persistant entity, in the best an implementation of {@link DefaultEntity}
 */
public interface AbstractService<T>  {
    /**
     * A simple fetch all implementation
     * @return list of all records of T
     */
    List<T> find();


    /**
     * Updates an object in DB
     * @param object - object to be updated, with ID
     * @return Updated T object
     * @throws ItemNotUserException Exceptions on thrown when trying to update and object by user, not owner of the object
     * @throws ItemNotFoundException When object cannot be found by id of the #object, this exception is thrown
     * @throws UnmodifiedItemSaveAttempt Exception says, that there are no changes in the object which should be updated
     * @throws ItemDoesNotContainsIdValue Exception is throw when Objects has null ID
     */
    T edit(T object) throws ItemNotUserException, ItemNotFoundException, UnmodifiedItemSaveAttempt, ItemDoesNotContainsIdValue;


    /**
     * Removes object from DB
     * @param id - id of the object to be removed
     * @throws ItemNotUserException Exceptions is thrown on trying to update and object by user, not owner of the object
     * @throws ItemNotFoundException When object cannot be found by id of the #object, this exception is thrown
     */
    void delete(Long id) throws ItemNotUserException, ItemNotFoundException;

    /**
     * Removes objects from DB
     * @param ids - ids of objects to be removed
     * @throws ItemNotUserException Exception is thrown when trying to update an object from a user, not owner of the object
     * @throws ItemNotFoundException When object cannot be found by id of the #object, this exception is thrown
     */
    void deleteItems(List<Long> ids) throws ItemNotUserException, ItemNotFoundException;

    /**
     * Before object is saved in DB this method is called
     * @param object - an object variant to be updated
     * @param original - an original object from DB
     * @return - a final object to be updated
     * @throws ItemNotFoundException When object cannot be found by id of the #object, this exception is thrown
     * @throws UnmodifiedItemSaveAttempt Exception says, that there are no changes in the object which should be updated
     */
    default T preEdit(T object, T original) throws UnmodifiedItemSaveAttempt, ItemNotFoundException {
        return object;
    }

    /**
     * Before object is added this method is called
     * @param object to be saved
     * @return a modified object before saving
     * @throws ItemForAddContainsIdException No id should be in edit object
     */
    default T preAdd(T object) throws ItemForAddContainsIdException {
        return object;
    }

    /**
     * What to do with object after it is stored in DB
     * @param object Saved object with ID
     * @return modified object
     */
    default T afterAdd(T object){
        return object;
    }

    /**
     * Verifies whether object exist or not ( normally, by Id)
     * @param object Object to check
     * @return true if object exists, false otherwise
     */
    boolean exists(T object);

    T save(T form) throws ItemForAddContainsIdException;

    Optional<T> findById(Long id);


    /**
     *
     * Returns all records from db having id > from (or ignoring if null) and limited of limit
     * Default method to get all values should be used with care or better not to be used !
     *
     */
    default List<T> find(Long from, Integer limit, Integer page) {

        PageRequest request = null;
        if (limit != null) {
            request = PageRequest.of(Objects.requireNonNullElse(page, 0), limit);
        }

        DefaultSearchSpecification<T> searchSpecification = null;

        if (from != null){
            searchSpecification = QueryCase.moreOrEquals(DefaultEntity.ID_FIELD, from ).specification();
        }

        if (searchSpecification != null) {
            if (request != null) {
                return getRepository().findAll(searchSpecification, request).getContent();
            } else {
                return getRepository().findAll(searchSpecification);
            }
        } else {
            if (request != null) {
                return getRepository().findAll(request).getContent();
            } else {
                return StreamSupport.stream(getRepository().findAll().spliterator(), false)
                        .collect(Collectors.toList());
            }
        }
    }

    PaginationRepository<T> getRepository();


}
