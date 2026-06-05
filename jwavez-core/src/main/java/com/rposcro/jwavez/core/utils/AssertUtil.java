package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.exceptions.AssertionException;

public class AssertUtil {

    public static void nonNull(Object object, String message) {
        if (object == null) {
            throw new AssertionException(message);
        }
    }
}
