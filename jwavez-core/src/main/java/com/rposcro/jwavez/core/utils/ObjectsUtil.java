package com.rposcro.jwavez.core.utils;

import java.util.function.Supplier;

public class ObjectsUtil {

    public static <T> T orDefault(T instance, T defaultInstance) {
        return instance != null ? instance : defaultInstance;
    }

    public static <T> T orDefault(T instance, Supplier<T> defaultSupplier) {
        return instance != null ? instance : defaultSupplier.get();
    }
}
