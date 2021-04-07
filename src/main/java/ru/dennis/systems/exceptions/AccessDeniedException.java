package ru.dennis.systems.exceptions;


public class AccessDeniedException extends Exception {
    public AccessDeniedException(String message){
        super(message);
    }
}
