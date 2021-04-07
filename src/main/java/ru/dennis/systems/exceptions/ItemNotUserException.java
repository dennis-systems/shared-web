package ru.dennis.systems.exceptions;

public class ItemNotUserException extends AccessDeniedException {

    public ItemNotUserException(){
        super("Object doesn't belong to user! ");
    }
}
