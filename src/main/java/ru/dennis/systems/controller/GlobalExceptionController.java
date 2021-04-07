package ru.dennis.systems.controller;

import ru.dennis.systems.exceptions.AccessDeniedException;
import ru.dennis.systems.exceptions.ItemAlreadyExists;
import ru.dennis.systems.exceptions.ItemNotFoundException;
import ru.dennis.systems.exceptions.UnmodifiedItemSaveAttempt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnmodifiedItemSaveAttempt.class)
    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    @ResponseBody
    public  String exc(UnmodifiedItemSaveAttempt ex){
        return ex.getMessage();
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ ItemNotFoundException.class})
    @ResponseBody
    public  String itemNotFound(Exception e){
        return  e.getMessage();
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({ AccessDeniedException.class})
    @ResponseBody
    public  String accessDenied(Exception e){
        return  e.getMessage();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ ItemAlreadyExists.class})
    @ResponseBody
    public  String alreadyExists(Exception e){
        return  e.getMessage();
    }





}
