package ru.dennis.systems.exceptions;

public class UserNotFoundException extends ItemNotFoundException {

    public UserNotFoundException(String innerNumber){
        super("User not found Exception " + innerNumber);
    }
}
