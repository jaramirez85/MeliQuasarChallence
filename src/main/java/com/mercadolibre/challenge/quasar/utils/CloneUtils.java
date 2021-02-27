package com.mercadolibre.challenge.quasar.utils;

import java.util.ArrayList;
import java.util.List;

public class CloneUtils {

    public static <T> List<List<T>> copyList(List<List<T>> list) {
        List<List<T>> clone = new ArrayList<>();
        for (List<T> strings : list)
            clone.add(new ArrayList<>(strings));
        return clone;
    }
}
