package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.model.BitLength;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BytesUtilTest {

    @Test
    public void testWriteMSBValue() {
        byte[] result = new byte[7];

        BytesUtil.writeMSBValue(result, 1, BitLength.BIT_LENGTH_32, 0xad00f59c);

        assertEquals((byte) 0xad, result[1]);
        assertEquals((byte) 0x00, result[2]);
        assertEquals((byte) 0xf5, result[3]);
        assertEquals((byte) 0x9c, result[4]);
    }

    @ParameterizedTest
    @MethodSource("extractValueArguments")
    public void testExtractValue(int value, int startBit, int mask, int result) {
        assertEquals((byte) result, BytesUtil.extractValue((byte) value, startBit, mask));
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
