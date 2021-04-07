package ru.dennis.systems.pojo_view;

public interface DefaultDataConverter<T, E> {
    default T convert(T object, E data){
        return object;
    }
}
