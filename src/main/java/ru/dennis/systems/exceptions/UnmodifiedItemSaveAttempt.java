package ru.dennis.systems.exceptions;

public class UnmodifiedItemSaveAttempt extends Exception {
    public UnmodifiedItemSaveAttempt(){}

    public UnmodifiedItemSaveAttempt (String s){
        super(s);
    }
}
