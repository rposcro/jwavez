package com.rposcro.jwavez.core.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitsUtilTest {

    @ParameterizedTest
    @MethodSource("extractValueArguments")
    public void testExtractValue(int value, int startBit, int mask, int result) {
        assertEquals((byte) result, BitsUtil.extractValue((byte) value, startBit, mask));
    }

    private static Stream<Arguments> extractValueArguments() {
        return Stream.of(
          Arguments.of(0b01010111, 4, 0b1111, 5),
          Arguments.of(0b01010111, 2, 0b111111, 21),
          Arguments.of(0b01010111, 0, 0b11, 3),
          Arguments.of(0b01010111, 0, 0b1111, 7),
          Arguments.of(0b11010111, 0, 0xff, 215)
        );
    }
}
