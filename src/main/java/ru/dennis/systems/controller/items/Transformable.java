package ru.dennis.systems.controller.items;

public interface Transformable<FORM_TYPE, DB_TYPE> {

    default DB_TYPE fromForm(FORM_TYPE form){
        return (DB_TYPE) form;
    }
    default FORM_TYPE toForm(DB_TYPE form){
        return (FORM_TYPE) form;
    }
}
