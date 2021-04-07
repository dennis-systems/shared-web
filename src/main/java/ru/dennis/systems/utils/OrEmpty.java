package ru.dennis.systems.utils;

public interface OrEmpty {
    String exec();

    default String or() {
        try {
            return exec();
        } catch (Exception ignored) {
            return "";
        }
    }
    default String orElse(OrEmpty orEmpty) {
        try {
            String res = exec();
            if (res == null) {
                return orEmpty.or();
            }
            return res;
        } catch (Exception ignored) {
            return "";
        }
    }
}

