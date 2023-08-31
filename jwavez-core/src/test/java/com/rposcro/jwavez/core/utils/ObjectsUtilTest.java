package com.rposcro.jwavez.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectsUtilTest {

    @Test
    public void returnsGivenInstanceWhenNotNull() {
        final String instance = "Instance";

        assertEquals(instance, ObjectsUtil.orDefault(instance, "Default"));
        assertEquals(instance, ObjectsUtil.orDefault(instance, () -> "Default"));
    }

    @Test
    public void returnsGivenInstanceWhenNull() {
        final String instance = "Default";

        assertEquals(instance, ObjectsUtil.orDefault(null, instance));
        assertEquals(instance, ObjectsUtil.orDefault(instance, () -> instance));
    }
}
