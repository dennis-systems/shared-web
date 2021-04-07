package ru.dennis.systems.exceptions;

import ru.dennis.systems.entity.DefaultEntity;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException (String s){
        super(s);
    }

    public <T extends DefaultEntity> ItemNotFoundException(DefaultEntity object) {
        super("Item of entity " + object.getClass().getSimpleName() + " not found with id" + object.getId());
    }

    public ItemNotFoundException(Long id) {
        super("Item with id " + id + " not found");
    }
}
