package com.rposcro.jwavez.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuffersUtilTest {

    @Test
    public void testAsByteArrayFromIntArray() {
        assertArrayEquals(new byte[0], BuffersUtil.asByteArray(new int[0]));
        assertArrayEquals(new byte[] { 0x12, (byte) 0xf7, 0x00, (byte) 0xaf, (byte) 0x05 },
                BuffersUtil.asByteArray(new int[] { 0x12, 0xf7, 0x00, 0x23af, 0xf9f05 }));
    }

    @ParameterizedTest
    @MethodSource("asByteArrayArguments")
    public void testAsByteArrayFromString(String input, byte[] expected) {
        assertArrayEquals(expected, BuffersUtil.asByteArray(input));
    }

    @ParameterizedTest
    @MethodSource("asStringArguments")
    public void testAsString(byte[] bytes, String expected) {
        String actual = BuffersUtil.asString(bytes);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("asConciseStringArguments")
    public void testAsConciseString(byte[] bytes, String expected) {
        String actual = BuffersUtil.asConciseString(bytes);
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> asStringArguments() {
        return Stream.of(
                Arguments.of(new byte[] { (byte) 0xd5, 0x65, 0x00 }, "d5 65 00"),
                Arguments.of(new byte[] { 0x00, (byte) 0x99, 0x17, 0x32 }, "00 99 17 32"),
                Arguments.of(new byte[] { }, "")
        );
    }

    private static Stream<Arguments> asConciseStringArguments() {
        return Stream.of(
                Arguments.of(new byte[] { (byte) 0xd5, 0x65, 0x00 }, "d56500"),
                Arguments.of(new byte[] { 0x00, (byte) 0x99, 0x17, 0x32 }, "00991732"),
                Arguments.of(new byte[] { }, "")
        );
    }

    private static Stream<Arguments> asByteArrayArguments() {
        return Stream.of(
                Arguments.of("d56500", new byte[] { (byte) 0xd5, 0x65, 0x00 }),
                Arguments.of(" d5 65  00", new byte[] { (byte) 0xd5, 0x65, 0x00 }),
                Arguments.of(" 5 98a065  ff", new byte[] { 0x05, (byte) 0x98, (byte) 0xa0, 0x65, (byte) 0xff }),
                Arguments.of("", new byte[0])
        );
    }
}
