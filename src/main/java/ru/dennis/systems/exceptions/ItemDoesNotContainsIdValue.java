package ru.dennis.systems.exceptions;

public class ItemDoesNotContainsIdValue extends Exception {
    public ItemDoesNotContainsIdValue(){
        super("Id should be set before editing item");
    }
}
