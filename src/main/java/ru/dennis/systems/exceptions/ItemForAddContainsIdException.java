package ru.dennis.systems.exceptions;

public class ItemForAddContainsIdException extends Exception {
    public ItemForAddContainsIdException(){
        super(" One or more elements in list contains ID, which is not allowed in add process, use edit instead");
    }
}
